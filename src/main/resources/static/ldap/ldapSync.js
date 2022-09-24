Ext.onReady(function () {
  // Ext.tip.QuickTipManager.init();

  var reload = function () {
    taskStore.load();
  };

  var info_panel = Ext.create('Ext.panel.Panel', {
    region: "north",
    height: 100,
    frame: false,
    border: false,
    bodyStyle: 'background:rgb(223,233,246)',
    html: 'loading...'
  });

  var taskStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../ldap/getTaskRecords.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['uuid', 'executeTime', 'result']
  });

  var taskGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: '任务执行列表',
    hasInfoBbar: true,
    hasBbar: false,
    store: taskStore,
    columns: [{
      dataIndex: 'uuid',
      header: "uuid",
      width: 250
    }, {
      dataIndex: 'executeTime',
      header: "执行时间",
      width: 160
    }, {
      dataIndex: 'result',
      header: "执行结果",
      flex: 1
    }],
    listeners: {
      itemdblclick: function (dv, record, item, index, e) {
        formWindow.getFormPanel().getForm().loadRecord(record);
        formWindow.show();
      }
    }
  });

  var formWindow = new MyExt.Component.FormWindow({
    title: 'info',
    width: 500,
    height: 250,
    formItems: [{
      fieldLabel: 'uuid',
      name: 'uuid',
      // cls: 'x-item-disabled',
      readOnly: true
    }, {
      fieldLabel: '执行时间',
      name: 'executeTime',
      readOnly: true
    }, {
      xtype: 'textarea',
      fieldLabel: '执行结果',
      name: 'result',
      readOnly: true
    }]
  });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [info_panel, taskGrid]
  });

  MyExt.util.Ajax("../ldap/getTaskInfo.do", null, function (data) {
    info_panel.body.update(data.data);
  });

  reload();

})