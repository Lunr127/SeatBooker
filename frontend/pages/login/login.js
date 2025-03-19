const app = getApp();

Page({
  data: {
    studentId: '',
    password: '',
    loading: false
  },
  
  onLoad: function() {
    // 页面加载时检查是否已登录
    if (app.globalData.token) {
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },
  
  // 输入学号
  inputStudentId: function(e) {
    this.setData({
      studentId: e.detail.value
    });
  },
  
  // 输入密码
  inputPassword: function(e) {
    this.setData({
      password: e.detail.value
    });
  },
  
  // 登录操作
  login: function() {
    const { studentId, password } = this.data;
    
    // 表单验证
    if (!studentId) {
      wx.showToast({
        title: '请输入学号',
        icon: 'none'
      });
      return;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ loading: true });
    
    // 发送登录请求
    wx.request({
      url: app.globalData.baseUrl + '/v1/user/login',
      method: 'POST',
      data: {
        studentId: studentId,
        password: password
      },
      success: (res) => {
        if (res.data.code === 200) {
          // 登录成功，保存token
          const token = res.data.data;
          wx.setStorageSync('token', token);
          app.globalData.token = token;
          
          // 获取用户信息
          app.getUserInfo();
          
          // 跳转到首页
          wx.switchTab({
            url: '/pages/index/index'
          });
        } else {
          // 登录失败
          wx.showToast({
            title: res.data.message || '登录失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },
  
  // 跳转到注册页
  goToRegister: function() {
    wx.navigateTo({
      url: '/pages/register/register'
    });
  }
}) 