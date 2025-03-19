const app = getApp();

Page({
  data: {
    userInfo: null,
    studyRooms: []
  },
  
  onLoad: function() {
    // 检查是否已登录
    if (!app.globalData.token) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.setData({
      userInfo: app.globalData.userInfo
    });
    
    // 获取自习室列表
    this.getStudyRooms();
  },
  
  onShow: function() {
    // 页面显示时检查登录状态
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo
      });
    }
    
    // 刷新自习室列表
    this.getStudyRooms();
  },
  
  getStudyRooms: function() {
    // 获取所有自习室列表
    app.request({
      url: '/v1/rooms',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            studyRooms: res.data.data
          });
        }
      }
    });
  },
  
  goToRoomDetail: function(e) {
    const roomId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/room/detail?id=' + roomId
    });
  },
  
  // 扫码签到
  scanQrCode: function() {
    wx.scanCode({
      success: (res) => {
        try {
          const data = JSON.parse(res.result);
          if (data.roomId && data.code) {
            wx.navigateTo({
              url: `/pages/checkin/checkin?roomId=${data.roomId}&code=${data.code}`
            });
          } else {
            wx.showToast({
              title: '无效的二维码',
              icon: 'none'
            });
          }
        } catch (e) {
          wx.showToast({
            title: '无效的二维码',
            icon: 'none'
          });
        }
      }
    });
  }
}) 