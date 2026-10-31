// 舍得前端公共脚本，页面都从 window.Shede 拿东西
// 外面包一层 IIFE，里面的函数别污染全局
(function (global) {
  'use strict';

  // 登录用户存 localStorage 的 key
  var STORAGE_KEY = 'shede_user';

  // 转义，拼 innerHTML 前都得过，不然会执行脚本
  function escapeHtml(value) {
    return String(value == null ? '' : value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  // 金额，带 ¥ 保留两位，别出现 ¥NaN
  function money(value) {
    return '¥' + Number(value || 0).toFixed(2);
  }

  // 日期，先把 - 换成 /，Safari 不认带 - 的字符串
  function formatDate(value) {
    if (!value) {
      return '-';
    }
    var date = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
    if (isNaN(date.getTime())) {
      return String(value);
    }
    function pad(n) { return (n < 10 ? '0' : '') + n; }
    return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate())
      + ' ' + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds());
  }

  // 所有接口都走这里，后端固定回 { success, code, message, data }
  // 成功只返 data，页面少写一层
  async function request(url, options) {
    var opts = options || {};
    var init = { method: opts.method || 'GET', headers: {} };
    if (opts.body !== undefined) {
      init.headers['Content-Type'] = 'application/json;charset=UTF-8';
      init.body = JSON.stringify(opts.body);
    }
    var response = await fetch(url, init);
    var json = null;
    try {
      json = await response.json();
    } catch (e) {
      json = null;
    }
    if (!json) {
      throw new Error('服务异常，请确认后端已启动（HTTP ' + response.status + '）');
    }
    if (!json.success) {
      var error = new Error(json.message || '请求失败');
      error.code = json.code;
      throw error;
    }
    return json.data;
  }

  // 接口都在这，路径跟后端 Controller 对齐，页面里别写死 /api/xxx
  var api = {
    // 商品列表，空值参数不拼进 query
    listProducts: function (params) {
      var query = new URLSearchParams();
      Object.keys(params || {}).forEach(function (key) {
        var value = params[key];
        if (value !== undefined && value !== null && value !== '') {
          query.append(key, value);
        }
      });
      return request('/api/product/list?' + query.toString());
    },
    // 商品详情，id 要 encode，不然会把 query 拆坏
    productDetail: function (productId) {
      return request('/api/product/detail?productId=' + encodeURIComponent(productId));
    },
    // 下单，参数走 JSON body
    createOrder: function (body) {
      return request('/api/order/create', { method: 'POST', body: body });
    },
    // 订单列表，status 空串就是全部，不传这个参数
    orders: function (userId, status) {
      var query = new URLSearchParams({ userId: userId });
      if (status !== undefined && status !== null && status !== '') {
        query.append('status', status);
      }
      return request('/api/order/list?' + query.toString());
    },
    // 关单，库存后端会加回去
    closeOrder: function (orderId) {
      return request('/api/pay/close?orderId=' + encodeURIComponent(orderId), { method: 'POST' });
    },
    // 发起支付，只拿表单 HTML，跳不跳由 submitAlipayForm 决定
    payOrder: function (orderId, userId) {
      return request('/api/pay/alipay', { method: 'POST', body: { orderId: orderId, userId: userId } });
    },
    // 登录，回来的是用户对象，带 userId
    login: function (body) {
      return request('/api/user/login', { method: 'POST', body: body });
    },
    // 注册，成功也回用户对象，注册完直接算登录
    register: function (body) {
      return request('/api/user/register', { method: 'POST', body: body });
    }
  };

  // 登录态就是 localStorage 里一个用户 JSON
  // 没有 token，后端别拿它鉴权，只能演示用
  var auth = {
    // 读用户，JSON 坏了就返 null，别让整页脚本崩
    getUser: function () {
      try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null');
      } catch (e) {
        return null;
      }
    },
    // 登录成功后写进去，刷新还在
    setUser: function (user) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    },
    // 退出就是删 key，后端没有登出接口
    logout: function () {
      localStorage.removeItem(STORAGE_KEY);
    }
  };

  // 小提示，2.2 秒后自己淡出
  // 用 textContent 不用 innerHTML，文案里有尖括号也没事
  function toast(message, type) {
    var host = document.getElementById('shede-toast');
    if (!host) {
      host = document.createElement('div');
      host.id = 'shede-toast';
      host.className = 'fixed left-1/2 bottom-10 z-[60] -translate-x-1/2 space-y-2 pointer-events-none';
      document.body.appendChild(host);
    }
    var item = document.createElement('div');
    // 报错用玫红，普通提示用深灰
    item.className = 'rounded-full px-5 py-2.5 text-sm font-medium text-white shadow-lg transition-all duration-300 opacity-0 translate-y-2 ' +
      (type === 'error' ? 'bg-rose-500' : 'bg-neutral-900');
    item.textContent = message;
    host.appendChild(item);
    // 等一帧再删 class，同一帧里改浏览器不会跑动画
    requestAnimationFrame(function () {
      item.classList.remove('opacity-0', 'translate-y-2');
    });
    setTimeout(function () {
      item.classList.add('opacity-0', 'translate-y-2');
      // 等 300ms 过渡结束再删节点，不然会啪一下没了
      setTimeout(function () { item.remove(); }, 300);
    }, 2200);
  }

  // 登录弹窗全页只建一次，后面靠显隐切
  // pendingHandler 存登录完要接着做的事
  var authModal = null;
  var pendingHandler = null;
  var mode = 'login';

  // 第一次要登录才建，不用登录的页面省点 DOM
  function buildAuthModal() {
    var wrapper = document.createElement('div');
    wrapper.id = 'shede-auth';
    wrapper.className = 'fixed inset-0 z-50 hidden items-center justify-center bg-neutral-900/40 px-4 backdrop-blur-sm';
    wrapper.innerHTML = [
      '<div class="w-full max-w-sm rounded-3xl bg-white p-6 shadow-2xl">',
      '  <div class="flex items-start justify-between">',
      '    <div>',
      '      <p class="text-xs font-semibold uppercase tracking-[0.2em] text-sky-500">Shede</p>',
      '      <h2 class="mt-1 text-xl font-bold" data-title>登录舍得</h2>',
      '    </div>',
      '    <button data-close class="rounded-full p-2 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-600" aria-label="关闭">',
      '      <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor"><path d="M6 6l8 8M14 6l-8 8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" fill="none"/></svg>',
      '    </button>',
      '  </div>',
      '  <div class="mt-4 grid grid-cols-2 gap-1 rounded-full bg-neutral-100 p-1 text-sm">',
      '    <button data-tab="login" class="rounded-full py-2 font-medium">登录</button>',
      '    <button data-tab="register" class="rounded-full py-2 font-medium">注册</button>',
      '  </div>',
      '  <form class="mt-5 space-y-3" data-form>',
      '    <label class="block">',
      '      <span class="text-xs text-neutral-500">用户名</span>',
      '      <input data-username required minlength="3" autocomplete="username"',
      '        class="mt-1 w-full rounded-xl border border-neutral-200 px-4 py-2.5 text-sm outline-none focus:border-sky-400 focus:ring-2 focus:ring-sky-100" placeholder="至少 3 位" />',
      '    </label>',
      '    <label class="block">',
      '      <span class="text-xs text-neutral-500">密码</span>',
      '      <input data-password type="password" required minlength="6" autocomplete="current-password"',
      '        class="mt-1 w-full rounded-xl border border-neutral-200 px-4 py-2.5 text-sm outline-none focus:border-sky-400 focus:ring-2 focus:ring-sky-100" placeholder="至少 6 位" />',
      '    </label>',
      '    <p data-error class="hidden text-xs text-rose-500"></p>',
      '    <button type="submit" class="w-full rounded-xl bg-sky-500 py-2.5 text-sm font-semibold text-white shadow-lg shadow-sky-200 transition hover:bg-sky-600">',
      '      <span data-submit-text>登录</span>',
      '    </button>',
      '  </form>',
      '  <p class="mt-3 text-center text-[11px] text-neutral-400">登录后可下单、查看订单</p>',
      '</div>'
    ].join('');
    document.body.appendChild(wrapper);

    // 事件挂在容器上，按钮是后面塞进来的，只能委托
    wrapper.addEventListener('click', function (event) {
      if (event.target === wrapper || event.target.closest('[data-close]')) {
        closeAuth();
      }
      var tab = event.target.closest('[data-tab]');
      if (tab) {
        mode = tab.getAttribute('data-tab');
        renderAuthMode();
      }
    });

    // 登录和注册只差接口，流程写一份
    wrapper.querySelector('[data-form]').addEventListener('submit', async function (event) {
      event.preventDefault();
      var username = wrapper.querySelector('[data-username]').value.trim();
      var password = wrapper.querySelector('[data-password]').value;
      var errorBox = wrapper.querySelector('[data-error]');
      var submitText = wrapper.querySelector('[data-submit-text]');
      errorBox.classList.add('hidden');
      submitText.textContent = '处理中...';
      try {
        var user = mode === 'login'
          ? await api.login({ username: username, password: password })
          : await api.register({ username: username, password: password });
        auth.setUser(user);
        closeAuth();
        toast(mode === 'login' ? '欢迎回来，' + user.username : '注册成功');
        // 登录成功后接着跑原来被拦下的操作
        // 先存局部变量再清空，免得 handler 里又登录一次被覆盖
        if (typeof pendingHandler === 'function') {
          var handler = pendingHandler;
          pendingHandler = null;
          handler(user);
        }
        // 通知页面刷自己的登录态 UI
        if (typeof global.Shede.onAuthChange === 'function') {
          global.Shede.onAuthChange(user);
        }
      } catch (e) {
        // 失败只在弹窗里提示，不关弹窗，改一下还能重试
        errorBox.textContent = e.message;
        errorBox.classList.remove('hidden');
      } finally {
        // 按钮文案必须还原，不然一直卡在处理中
        submitText.textContent = mode === 'login' ? '登录' : '注册';
      }
    });

    return wrapper;
  }

  // 切登录/注册，只改文案和 class，不重建 DOM
  function renderAuthMode() {
    if (!authModal) {
      return;
    }
    var isLogin = mode === 'login';
    authModal.querySelector('[data-title]').textContent = isLogin ? '登录舍得' : '注册舍得';
    authModal.querySelector('[data-submit-text]').textContent = isLogin ? '登录' : '注册';
    authModal.querySelectorAll('[data-tab]').forEach(function (tab) {
      var active = tab.getAttribute('data-tab') === mode;
      tab.className = 'rounded-full py-2 font-medium ' + (active ? 'bg-white text-sky-600 shadow' : 'text-neutral-500');
    });
  }

  // 打开弹窗，handler 是登录完要接着做的事，可不传
  function openAuth(handler) {
    pendingHandler = handler || null;
    if (!authModal) {
      authModal = buildAuthModal();
    }
    mode = 'login';
    renderAuthMode();
    // hidden 和 flex 成对切，别直接改 style
    authModal.classList.remove('hidden');
    authModal.classList.add('flex');
  }

  // 关弹窗，顺手把上次的错误提示清掉
  function closeAuth() {
    if (!authModal) {
      return;
    }
    authModal.classList.add('hidden');
    authModal.classList.remove('flex');
    var errorBox = authModal.querySelector('[data-error]');
    if (errorBox) {
      errorBox.classList.add('hidden');
    }
  }

  // 没登录先弹窗，登录完继续原来的操作
  // 不然登录回来还得再点一次，上下文丢了
  function requireLogin(handler) {
    var user = auth.getUser();
    if (user) {
      handler(user);
      return;
    }
    openAuth(handler);
  }

  // 支付遮罩，只建一次
  var overlayEl = null;

  // 遮罩里有个默认隐藏的手动继续按钮
  function ensureOverlay() {
    if (overlayEl) {
      return overlayEl;
    }
    overlayEl = document.createElement('div');
    overlayEl.id = 'shede-pay-overlay';
    overlayEl.className = 'fixed inset-0 z-[70] hidden items-center justify-center bg-neutral-900/50 px-4 backdrop-blur-sm';
    overlayEl.innerHTML = '' +
      '<div class="w-full max-w-xs rounded-3xl bg-white p-8 text-center shadow-2xl">' +
      '  <div class="mx-auto h-9 w-9 animate-spin rounded-full border-2 border-sky-100 border-t-sky-500"></div>' +
      '  <p class="mt-4 text-sm font-semibold">正在跳转支付宝收银台…</p>' +
      '  <p class="mt-1 text-[11px] text-neutral-400">如未自动跳转，请点击下方按钮</p>' +
      '  <button id="shede-pay-manual" class="mt-4 hidden w-full rounded-full bg-sky-500 py-2.5 text-sm font-semibold text-white">手动继续</button>' +
      '</div>';
    document.body.appendChild(overlayEl);
    return overlayEl;
  }

  // 显示遮罩
  function showOverlay() {
    var el = ensureOverlay();
    el.classList.remove('hidden');
    el.classList.add('flex');
  }

  // 收起遮罩，从支付宝返回也要调，不然一直转圈
  function hideOverlay() {
    if (!overlayEl) {
      return;
    }
    overlayEl.classList.add('hidden');
    overlayEl.classList.remove('flex');
  }

  // 提交支付宝表单：不能直接 holder.innerHTML = html
  // innerHTML 塞进来的 script 浏览器不执行，那段自动提交永远不跑
  // 所以先用 DOMParser 把 HTML 当数据解析，抠出 form，再自己 submit
  function submitAlipayForm(html) {
    // 藏起来的容器，只放 form，别直接给 form 设 display:none
    var holder = document.getElementById('shede-alipay-holder');
    if (!holder) {
      holder = document.createElement('div');
      holder.id = 'shede-alipay-holder';
      holder.className = 'hidden';
      document.body.appendChild(holder);
    }
    // 这一步不跑任何脚本，等于安全地把 HTML 当数据看
    var doc = new DOMParser().parseFromString(html, 'text/html');
    var form = doc.querySelector('form');
    if (!form) {
      // 抠不到 form 说明后端回的东西不对，退一步开新窗口 write
      var win = window.open('about:blank', '_blank');
      if (win) {
        win.document.open();
        win.document.write(html);
        win.document.close();
        return;
      }
      throw new Error('支付表单解析失败，请稍后重试');
    }
    // form 得拷进当前文档才能 submit
    holder.innerHTML = '';
    holder.appendChild(document.importNode(form, true));
    var injected = holder.querySelector('form');
    // 就在当前页跳，别开新窗口不然容易被拦
    injected.setAttribute('target', '_self');

    // 先让遮罩画出来再提交，跳太快看不到
    showOverlay();
    var manual = document.getElementById('shede-pay-manual');
    manual.classList.remove('hidden');
    manual.onclick = function () { injected.submit(); };
    setTimeout(function () { injected.submit(); }, 300);
  }

  // 支付：先拿表单再提交，页面只 await 这一个
  async function payOrder(orderId, userId) {
    var payResult = await api.payOrder(orderId, userId);
    submitAlipayForm(payResult.form);
    return payResult;
  }

  // 从支付宝按返回，浏览器可能走 bfcache，这里主动收遮罩
  window.addEventListener('pageshow', function () { hideOverlay(); });

  // 对外就暴露这一组，页面别碰内部函数
  global.Shede = {
    api: api,
    auth: auth,
    toast: toast,
    money: money,
    formatDate: formatDate,
    escapeHtml: escapeHtml,
    openAuth: openAuth,
    closeAuth: closeAuth,
    requireLogin: requireLogin,
    // 支付单独放一个对象，submitForm 只管提交，pay 是完整流程
    alipay: { submitForm: submitAlipayForm, pay: payOrder, hideOverlay: hideOverlay },
    // 页面自己覆盖这个钩子，登录态变了刷 UI
    onAuthChange: null
  };
})(window);
