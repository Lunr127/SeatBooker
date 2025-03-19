App({
  globalData: {
    baseUrl: 'http://localhost:8080/api',
    userInfo: null,
    token: null
  },
  
  onLaunch: function() {
    // 获取本地存储的token
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.getUserInfo();
    }
  },
  
  getUserInfo: function() {
    // 获取用户信息
    wx.request({
      url: this.globalData.baseUrl + '/v1/user/info',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + this.globalData.token
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.globalData.userInfo = res.data.data;
        } else {
          // token失效，重新登录
          wx.removeStorageSync('token');
          this.globalData.token = null;
          this.globalData.userInfo = null;
          wx.navigateTo({
            url: '/pages/login/login'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      }
    });
  },
  
  // 请求封装，处理token等
  request: function(options) {
    const token = this.globalData.token;
    const header = options.header || {};
    
    if (token) {
      header['Authorization'] = 'Bearer ' + token;
    }
    
    wx.request({
      url: this.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      success: (res) => {
        // token失效处理
        if (res.data.code === 401) {
          wx.removeStorageSync('token');
          this.globalData.token = null;
          this.globalData.userInfo = null;
          
          wx.navigateTo({
            url: '/pages/login/login'
          });
          return;
        }
        
        options.success && options.success(res);
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
        options.fail && options.fail(err);
      },
      complete: options.complete
    });
  }
}) 