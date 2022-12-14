Ext.onReady(function () {
  // Ext.tip.QuickTipManager.init();

  var reload = function () {
    userStore.load();
  };

  var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../ldap/searchLdapUser.do',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['objectClass', 'distinguishedName', 'jsonString']
  });

  userStore.on('beforeload', function (store, options) {
    options.params = Ext.apply(options.params || {}, searchForm.getForm().getValues());
  });

  var searchForm = Ext.create('Ext.form.Panel', {
    region: 'north',
    frame: true,
    height: 120,
    bodyStyle: 'padding:15px 0px 0px 10px',
    fieldDefaults: {
      labelWidth: 80
    },
    defaults: {
      width: 300
    },
    defaultType: 'textfield',
    buttonAlign: 'left',
    items: [{
      fieldLabel: 'LDAP filter',
      width: 600,
      emptyText: '(objectClass=user)',
      name: 'ldapfilter',
      enableKeyEvents: true,
      listeners: {
        keypress: function (thiz, e) {
          if (e.getKey() == Ext.EventObject.ENTER) {
            userGrid.getPageToolbar().moveFirst();
          }
        }
      }
    }, {
      fieldLabel: 'LDAP base',
      width: 600,
      emptyText: 'ou=hangzhou,dc=test,dc=com',
      name: 'ldapbase',
      enableKeyEvents: true,
      listeners: {
        keypress: function (thiz, e) {
          if (e.getKey() == Ext.EventObject.ENTER) {
            userGrid.getPageToolbar().moveFirst();
          }
        }
      }
    }, {
      xtype: 'fieldcontainer',
      fieldLabel: '高级选项',
      defaultType: 'checkboxfield',
      items: [
        {
          boxLabel: '在SCIM服务端同步删除未选择用户',
          name: 'removeunselected',
          inputValue: true,
          checked: false,
          id: 'checkbox_remove_unselected'
        }
      ]
    }]
  });


  var userGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: 'LDAP列表',
    hasInfoBbar: true,
    hasBbar: false,
    store: userStore,
    columns: [{
      dataIndex: 'objectClass',
      header: "objectClass",
      width: 80
    }, {
      dataIndex: 'distinguishedName',
      header: "DN",
      width: 300
    }, {
      dataIndex: 'jsonString',
      header: "json",
      flex: 1
    }],
    tbar: [{
      text: '选择并同步到SCIM',
      iconCls: 'MyExt-refresh',
      handler: function () {
        var select = MyExt.util.SelectGridModel(userGrid, false);
        if (!select) {
          return;
        }
        let distinguishedNameArray = new Array();
        for (let i = 0; i < select.length; i++) {
          distinguishedNameArray[i] = select[i].data["distinguishedName"];
        }
        var removeUnselected = searchForm.getForm().getValues()["removeunselected"] === true;
        var tip = removeUnselected ? '同步会覆盖SCIM服务端数据,并在SCIM服务端删除未选择的用户' : '所选择的用户/组数据会覆盖SCIM服务端对应的用户/组'
        MyExt.util.MessageConfirm(tip + ',是否确定同步', function () {
          MyExt.util.Ajax('../ldap/syncChoose.do', {
            distinguishedNameArray: Ext.JSON.encode(distinguishedNameArray),
            ldapbase: searchForm.getForm().getValues()["ldapbase"],
            ldapfilter: searchForm.getForm().getValues()["ldapfilter"],
            removeUnselected: removeUnselected,
          }, function (data) {
            reload();
            MyExt.Msg.alert(data.data);

          });
        });
      }
    }, {
      text: '全部同步到SCIM',
      iconCls: 'MyExt-refresh',
      handler: function () {
        var removeUnselected = searchForm.getForm().getValues()["removeunselected"] === true;
        var tip = removeUnselected ? '同步会覆盖SCIM服务端数据，不在该搜索范围的数据会被删除' : '同步该搜索条件下所有用户/组到SCIM服务端并覆盖对应的用户/组';
        MyExt.util.MessageConfirm(tip + ',是否确定同步', function () {
          MyExt.util.Ajax('../ldap/syncSearch.do', {
            ldapbase: searchForm.getForm().getValues()["ldapbase"],
            ldapfilter: searchForm.getForm().getValues()["ldapfilter"],
            removeUnselected: removeUnselected,
          }, function (data) {
            reload();
            MyExt.Msg.alert(data.data);
          });
        });
      }
    }]
  });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [searchForm, userGrid]
  });

  MyExt.util.Ajax('../ldap/getSearchParams.do', null, function (data) {
    searchForm.getForm().setValues(data.data);
    reload();
  });

})