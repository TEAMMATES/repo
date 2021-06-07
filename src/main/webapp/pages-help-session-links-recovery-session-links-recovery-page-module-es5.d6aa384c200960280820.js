function _get(e,t,n){return(_get="undefined"!=typeof Reflect&&Reflect.get?Reflect.get:function(e,t,n){var r=_superPropBase(e,t);if(r){var i=Object.getOwnPropertyDescriptor(r,t);return i.get?i.get.call(n):i.value}})(e,t,n||e)}function _superPropBase(e,t){for(;!Object.prototype.hasOwnProperty.call(e,t)&&null!==(e=_getPrototypeOf(e)););return e}function _inherits(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&_setPrototypeOf(e,t)}function _setPrototypeOf(e,t){return(_setPrototypeOf=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,t)}function _createSuper(e){var t=_isNativeReflectConstruct();return function(){var n,r=_getPrototypeOf(e);if(t){var i=_getPrototypeOf(this).constructor;n=Reflect.construct(r,arguments,i)}else n=r.apply(this,arguments);return _possibleConstructorReturn(this,n)}}function _possibleConstructorReturn(e,t){return!t||"object"!=typeof t&&"function"!=typeof t?_assertThisInitialized(e):t}function _assertThisInitialized(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}function _isNativeReflectConstruct(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}function _getPrototypeOf(e){return(_getPrototypeOf=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function _defineProperties(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}function _createClass(e,t,n){return t&&_defineProperties(e.prototype,t),n&&_defineProperties(e,n),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[47],{Wsbs:function(e,t,n){"use strict";n.d(t,"a",(function(){return i}));var r=n("fXoL"),i=function(){var e=function(){function e(){_classCallCheck(this,e),this.useBlueSpinner=!1}return _createClass(e,[{key:"ngOnInit",value:function(){}}]),e}();return e.\u0275fac=function(t){return new(t||e)},e.\u0275cmp=r.Kb({type:e,selectors:[["tm-ajax-loading"]],inputs:{useBlueSpinner:"useBlueSpinner"},decls:2,vars:3,consts:[[1,"loading-container"],["role","status"]],template:function(e,t){1&e&&(r.Wb(0,"div",0),r.Rb(1,"div",1),r.Vb()),2&e&&(r.Cb(1),r.Fb("spinner-border spinner-border-sm text-",t.useBlueSpinner?"primary":"white",""))},styles:[".loading-container[_ngcontent-%COMP%]{display:inline;margin-right:10px}"]}),e}()},oGan:function(e,t,n){"use strict";n.r(t),n.d(t,"SessionLinksRecoveryPageModule",(function(){return L}));var r,i,a,o,c,s=n("ofXK"),u=n("3Pt+"),l=n("tyNb"),h=n("fXoL"),p=["captchaWrapperElem"],f=((r=function(){function e(t,n,r,i){_classCallCheck(this,e),this.renderer=t,this.zone=n,this.injector=r,this.scriptService=i,this.captchaElemPrefix="ngx_captcha_id_",this.setupCaptcha=!0,this.useGlobalDomain=!1,this.type="image",this.tabIndex=0,this.success=new h.n,this.load=new h.n,this.reset=new h.n,this.ready=new h.n,this.error=new h.n,this.expire=new h.n,this.setupAfterLoad=!1,this.resetCaptchaAfterSuccess=!1,this.isLoaded=!1}return _createClass(e,[{key:"ngAfterViewInit",value:function(){this.control=this.injector.get(u.o).control}},{key:"ngAfterViewChecked",value:function(){this.setupCaptcha&&(this.setupCaptcha=!1,this.setupComponent())}},{key:"ngOnChanges",value:function(e){e&&e.hl&&(e.hl.firstChange||e.hl.currentValue===e.hl.previousValue||this.scriptService.cleanup()),e&&e.useGlobalDomain&&(e.useGlobalDomain.firstChange||e.useGlobalDomain.currentValue===e.useGlobalDomain.previousValue||this.scriptService.cleanup()),this.setupCaptcha=!0}},{key:"getResponse",value:function(){return this.reCaptchaApi.getResponse(this.captchaId)}},{key:"getCaptchaId",value:function(){return this.captchaId}},{key:"resetCaptcha",value:function(){var e=this;this.zone.run((function(){e.reCaptchaApi.reset(),e.onChange(void 0),e.onTouched(void 0),e.reset.next()}))}},{key:"getCurrentResponse",value:function(){return this.currentResponse}},{key:"reloadCaptcha",value:function(){this.setupComponent()}},{key:"ensureCaptchaElem",value:function(e){var t=document.getElementById(e);if(!t)throw Error("Captcha element with id '".concat(e,"' was not found"));this.captchaElem=t}},{key:"renderReCaptcha",value:function(){var e=this;this.zone.runOutsideAngular((function(){e.captchaId=e.reCaptchaApi.render(e.captchaElemId,e.getCaptchaProperties()),e.ready.next()}))}},{key:"handleCallback",value:function(e){var t=this;this.currentResponse=e,this.success.next(e),this.zone.run((function(){t.onChange(e),t.onTouched(e)})),this.resetCaptchaAfterSuccess&&this.resetCaptcha()}},{key:"getPseudoUniqueNumber",value:function(){return(new Date).getUTCMilliseconds()+Math.floor(9999*Math.random())}},{key:"setupComponent",value:function(){var e=this;this.captchaSpecificSetup(),this.createAndSetCaptchaElem(),this.scriptService.registerCaptchaScript(this.useGlobalDomain,"explicit",(function(t){e.onloadCallback(t)}),this.hl)}},{key:"onloadCallback",value:function(e){if(this.reCaptchaApi=e,!this.reCaptchaApi)throw Error("ReCaptcha Api was not initialized correctly");this.isLoaded=!0,this.load.next(),this.renderReCaptcha(),this.setupAfterLoad&&(this.setupAfterLoad=!1,this.setupComponent())}},{key:"generateNewElemId",value:function(){return this.captchaElemPrefix+this.getPseudoUniqueNumber()}},{key:"createAndSetCaptchaElem",value:function(){if(this.captchaElemId=this.generateNewElemId(),!this.captchaElemId)throw Error("Captcha elem Id is not set");if(!this.captchaWrapperElem)throw Error("Captcha DOM element is not initialized");this.captchaWrapperElem.nativeElement.innerHTML="";var e=this.renderer.createElement("div");e.id=this.captchaElemId,this.renderer.appendChild(this.captchaWrapperElem.nativeElement,e),this.ensureCaptchaElem(this.captchaElemId)}},{key:"writeValue",value:function(e){}},{key:"registerOnChange",value:function(e){this.onChange=e}},{key:"registerOnTouched",value:function(e){this.onTouched=e}},{key:"handleErrorCallback",value:function(){var e=this;this.zone.run((function(){e.onChange(void 0),e.onTouched(void 0)})),this.error.next()}},{key:"handleExpireCallback",value:function(){this.expire.next(),this.resetCaptcha()}}]),e}()).\u0275fac=function(e){h.hc()},r.\u0275dir=h.Lb({type:r,inputs:{useGlobalDomain:"useGlobalDomain",type:"type",tabIndex:"tabIndex",siteKey:"siteKey",hl:"hl"},outputs:{success:"success",load:"load",reset:"reset",ready:"ready",error:"error",expire:"expire"},features:[h.Ab]}),r),d=function(){var e={InvisibleReCaptcha:0,ReCaptcha2:1};return e[e.InvisibleReCaptcha]="InvisibleReCaptcha",e[e.ReCaptcha2]="ReCaptcha2",e}(),b=((c=function(){function e(t){_classCallCheck(this,e),this.zone=t,this.windowGrecaptcha="grecaptcha",this.windowOnLoadCallbackProperty="ngx_captcha_onload_callback",this.globalDomain="recaptcha.net",this.defaultDomain="google.com"}return _createClass(e,[{key:"registerCaptchaScript",value:function(e,t,n,r){var i=this;if(this.grecaptchaScriptLoaded())this.zone.run((function(){n(window[i.windowGrecaptcha])}));else{window[this.windowOnLoadCallbackProperty]=function(){return i.zone.run(n.bind(i,window[i.windowGrecaptcha]))};var a=document.createElement("script");a.innerHTML="",a.src=this.getCaptchaScriptUrl(e,t,r),a.async=!0,a.defer=!0,document.getElementsByTagName("head")[0].appendChild(a)}}},{key:"cleanup",value:function(){window[this.windowOnLoadCallbackProperty]=void 0,window[this.windowGrecaptcha]=void 0}},{key:"grecaptchaScriptLoaded",value:function(){return!(!window[this.windowOnLoadCallbackProperty]||!window[this.windowGrecaptcha])}},{key:"getLanguageParam",value:function(e){return e?"&hl="+e:""}},{key:"getCaptchaScriptUrl",value:function(e,t,n){return"https://www.".concat(e?this.globalDomain:this.defaultDomain,"/recaptcha/api.js?onload=").concat(this.windowOnLoadCallbackProperty,"&render=").concat(t).concat(this.getLanguageParam(n))}}]),e}()).\u0275fac=function(e){return new(e||c)(h.ec(h.z))},c.\u0275prov=h.Mb({token:c,factory:c.\u0275fac}),c),m=((o=function(e){_inherits(n,e);var t=_createSuper(n);function n(e,r,i,a){var o;return _classCallCheck(this,n),(o=t.call(this,e,r,i,a)).renderer=e,o.zone=r,o.injector=i,o.scriptService=a,o.windowOnErrorCallbackProperty="ngx_captcha_error_callback",o.windowOnExpireCallbackProperty="ngx_captcha_expire_callback",o.theme="light",o.size="normal",o.recaptchaType=d.ReCaptcha2,o}return _createClass(n,[{key:"ngOnChanges",value:function(e){_get(_getPrototypeOf(n.prototype),"ngOnChanges",this).call(this,e)}},{key:"ngOnDestroy",value:function(){window[this.windowOnErrorCallbackProperty]={},window[this.windowOnExpireCallbackProperty]={}}},{key:"captchaSpecificSetup",value:function(){this.registerCallbacks()}},{key:"getCaptchaProperties",value:function(){var e=this;return{sitekey:this.siteKey,callback:function(t){return e.zone.run((function(){return e.handleCallback(t)}))},"expired-callback":function(){return e.zone.run((function(){return e.handleExpireCallback()}))},"error-callback":function(){return e.zone.run((function(){return e.handleErrorCallback()}))},theme:this.theme,type:this.type,size:this.size,tabindex:this.tabIndex}}},{key:"registerCallbacks",value:function(){window[this.windowOnErrorCallbackProperty]=_get(_getPrototypeOf(n.prototype),"handleErrorCallback",this).bind(this),window[this.windowOnExpireCallbackProperty]=_get(_getPrototypeOf(n.prototype),"handleExpireCallback",this).bind(this)}}]),n}(f)).\u0275fac=function(e){return new(e||o)(h.Qb(h.D),h.Qb(h.z),h.Qb(h.r),h.Qb(b))},o.\u0275cmp=h.Kb({type:o,selectors:[["ngx-recaptcha2"]],viewQuery:function(e,t){var n;1&e&&h.Wc(p,!0),2&e&&h.Bc(n=h.jc())&&(t.captchaWrapperElem=n.first)},inputs:{theme:"theme",size:"size",hl:"hl"},features:[h.Bb([{provide:u.n,useExisting:Object(h.U)((function(){return o})),multi:!0}]),h.zb,h.Ab],decls:2,vars:0,consts:[["captchaWrapperElem",""]],template:function(e,t){1&e&&h.Rb(0,"div",null,0)},encapsulation:2}),o),y=((a=function(){function e(t,n){_classCallCheck(this,e),this.scriptService=t,this.zone=n}return _createClass(e,[{key:"execute",value:function(e,t,n,r){this.executeAsPromise(e,t,r).then(n)}},{key:"executeAsPromise",value:function(e,t,n){var r=this;return new Promise((function(i,a){r.scriptService.registerCaptchaScript(!(!n||!n.useGlobalDomain),e,(function(n){r.zone.runOutsideAngular((function(){try{n.execute(e,{action:t}).then((function(e){return r.zone.run((function(){return i(e)}))}))}catch(o){a(o)}}))}))}))}}]),e}()).\u0275fac=function(e){return new(e||a)(h.ec(b),h.ec(h.z))},a.\u0275prov=h.Mb({token:a,factory:a.\u0275fac}),a),v=((i=function e(){_classCallCheck(this,e)}).\u0275mod=h.Ob({type:i}),i.\u0275inj=h.Nb({factory:function(e){return new(e||i)},providers:[b,y],imports:[[s.c]]}),i),g=n("n6pr"),C=n("nYR2"),w=n("dOUP"),k=n("MbNv"),S=n("Uqoa"),_=n("Wsbs"),E=["captchaElem"];function O(e,t){if(1&e){var n=h.Xb();h.Wb(0,"div",10),h.Wb(1,"ngx-recaptcha2",11,12),h.ic("success",(function(e){return h.Fc(n),h.kc().handleSuccess(e)})),h.Vb(),h.Vb()}if(2&e){var r=h.kc();h.Cb(1),h.sc("siteKey",r.captchaSiteKey)("useGlobalDomain",!1)("size",r.size)("hl",r.lang)}}function P(e,t){1&e&&h.Rb(0,"tm-ajax-loading")}var R,x,z=[{path:"",component:(R=function(){function e(t,n,r){_classCallCheck(this,e),this.feedbackSessionsService=t,this.statusMessageService=n,this.formBuilder=r,this.captchaSuccess=!1,this.size="normal",this.lang="en",this.isFormSubmitting=!1,this.captchaSiteKey=w.a.captchaSiteKey}return _createClass(e,[{key:"ngOnInit",value:function(){this.formSessionLinksRecovery=this.formBuilder.group({email:["",u.D.required],recaptcha:[""]})}},{key:"onSubmitFormSessionLinksRecovery",value:function(e){var t=this;this.captchaSiteKey||(this.captchaResponse=""),this.formSessionLinksRecovery.valid&&void 0!==this.captchaResponse?(this.isFormSubmitting=!0,this.feedbackSessionsService.sendFeedbackSessionLinkToRecoveryEmail({sessionLinksRecoveryEmail:e.controls.email.value,captchaResponse:this.captchaResponse}).pipe(Object(C.a)((function(){return t.isFormSubmitting=!1}))).subscribe((function(e){e.isEmailSent?t.statusMessageService.showSuccessToast(e.message):t.statusMessageService.showErrorToast(e.message)}),(function(e){t.statusMessageService.showErrorToast(e.error.message)})),this.resetFormGroups()):this.statusMessageService.showErrorToast("Please enter a valid email address and click the reCAPTCHA before submitting.")}},{key:"resetFormGroups",value:function(){this.formSessionLinksRecovery=this.formBuilder.group({email:["",u.D.required],recaptcha:[""]}),this.reloadCaptcha()}},{key:"reloadCaptcha",value:function(){this.captchaSiteKey&&this.captchaElem.reloadCaptcha()}},{key:"handleSuccess",value:function(e){this.captchaSuccess=!0,this.captchaResponse=e}}]),e}(),R.\u0275fac=function(e){return new(e||R)(h.Qb(k.a),h.Qb(S.a),h.Qb(u.e))},R.\u0275cmp=h.Kb({type:R,selectors:[["tm-session-links-recovery-page"]],viewQuery:function(e,t){var n;1&e&&h.Wc(E,!0),2&e&&h.Bc(n=h.jc())&&(t.captchaElem=n.first)},decls:17,vars:3,consts:[[1,"color-orange"],[1,"card","bg-light","top-padded","col-sm-9","col-md-6"],[1,"form","form-horizontal",3,"formGroup","ngSubmit"],[1,"form-group","form-row","top-padded"],[1,"control-label"],["type","email","formControlName","email","maxlength","254","email","true","spellcheck","false",1,"form-control"],["class","form-row form-group top-padded",4,"ngIf"],[1,"form-row","top-padded"],["type","submit",1,"btn","btn-primary"],[4,"ngIf"],[1,"form-row","form-group","top-padded"],["formControlName","recaptcha",3,"siteKey","useGlobalDomain","size","hl","success"],["captchaElem",""]],template:function(e,t){1&e&&(h.Wb(0,"main"),h.Wb(1,"h1",0),h.Qc(2," Recovering Session Links for Students "),h.Vb(),h.Wb(3,"p"),h.Qc(4," If you cannot find your feedback session links, enter your course registered email below. Links to all your feedback sessions over the past 6 months will be sent to the email address. "),h.Vb(),h.Wb(5,"div",1),h.Wb(6,"form",2),h.ic("ngSubmit",(function(){return t.onSubmitFormSessionLinksRecovery(t.formSessionLinksRecovery)})),h.Wb(7,"div",3),h.Wb(8,"label",4),h.Wb(9,"strong"),h.Qc(10,"Email:"),h.Vb(),h.Vb(),h.Rb(11,"input",5),h.Vb(),h.Oc(12,O,3,4,"div",6),h.Wb(13,"div",7),h.Wb(14,"button",8),h.Oc(15,P,1,0,"tm-ajax-loading",9),h.Qc(16,"Submit "),h.Vb(),h.Vb(),h.Vb(),h.Vb(),h.Vb()),2&e&&(h.Cb(6),h.sc("formGroup",t.formSessionLinksRecovery),h.Cb(6),h.sc("ngIf",""!==t.captchaSiteKey),h.Cb(3),h.sc("ngIf",t.isFormSubmitting))},directives:[u.F,u.q,u.i,u.c,u.p,u.g,u.k,u.d,s.u,m,_.a],styles:[".top-padded[_ngcontent-%COMP%]{margin-top:20px}.form[_ngcontent-%COMP%]{padding-bottom:20px}"]}),R)}],L=((x=function e(){_classCallCheck(this,e)}).\u0275mod=h.Ob({type:x}),x.\u0275inj=h.Nb({factory:function(e){return new(e||x)},imports:[[s.c,u.j,u.z,v,l.h.forChild(z),g.a]]}),x)}}]);