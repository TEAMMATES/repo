function _classCallCheck(n,e){if(!(n instanceof e))throw new TypeError("Cannot call a class as a function")}(window.webpackJsonp=window.webpackJsonp||[]).push([[44],{"HuC+":function(n,e,t){"use strict";t.r(e),t.d(e,"AdminPagesModule",(function(){return c}));var a=t("ofXK"),o=t("tyNb"),i=t("dNX/"),l=t("RvxK"),r=t("fXoL"),u=[{path:"home",loadChildren:function(){return Promise.all([t.e(0),t.e(22)]).then(t.bind(null,"qt4E")).then((function(n){return n.AdminHomePageModule}))},data:{pageTitle:"Add New Instructor"}},{path:"accounts",loadChildren:function(){return Promise.all([t.e(0),t.e(21)]).then(t.bind(null,"vk7v")).then((function(n){return n.AdminAccountsPageModule}))},data:{pageTitle:"Account Details"}},{path:"search",loadChildren:function(){return Promise.all([t.e(1),t.e(2),t.e(3),t.e(4),t.e(0),t.e(23)]).then(t.bind(null,"/tDa")).then((function(n){return n.AdminSearchPageModule}))},data:{pageTitle:"Admin Search"}},{path:"sessions",loadChildren:function(){return Promise.all([t.e(1),t.e(2),t.e(3),t.e(4),t.e(24)]).then(t.bind(null,"HnTE")).then((function(n){return n.AdminSessionsPageModule}))},data:{pageTitle:"Ongoing Sessions"}},{path:"timezone",loadChildren:function(){return Promise.all([t.e(1),t.e(2),t.e(25)]).then(t.bind(null,"dmP9")).then((function(n){return n.AdminTimezonePageModule}))}},{path:"",pathMatch:"full",redirectTo:"home"},{path:"**",pathMatch:"full",component:i.a}],c=function(){var n=function n(){_classCallCheck(this,n)};return n.\u0275mod=r.Ob({type:n}),n.\u0275inj=r.Nb({factory:function(e){return new(e||n)},imports:[[a.c,l.a,o.h.forChild(u)]]}),n}()}}]);