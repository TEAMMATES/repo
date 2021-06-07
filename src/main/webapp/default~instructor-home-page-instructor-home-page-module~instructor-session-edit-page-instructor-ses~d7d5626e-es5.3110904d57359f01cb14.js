function _createForOfIteratorHelper(e,t){var i;if("undefined"==typeof Symbol||null==e[Symbol.iterator]){if(Array.isArray(e)||(i=_unsupportedIterableToArray(e))||t&&e&&"number"==typeof e.length){i&&(e=i);var s=0,n=function(){};return{s:n,n:function(){return s>=e.length?{done:!0}:{done:!1,value:e[s++]}},e:function(e){throw e},f:n}}throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}var o,r=!0,T=!1;return{s:function(){i=e[Symbol.iterator]()},n:function(){var e=i.next();return r=e.done,e},e:function(e){T=!0,o=e},f:function(){try{r||null==i.return||i.return()}finally{if(T)throw o}}}}function _unsupportedIterableToArray(e,t){if(e){if("string"==typeof e)return _arrayLikeToArray(e,t);var i=Object.prototype.toString.call(e).slice(8,-1);return"Object"===i&&e.constructor&&(i=e.constructor.name),"Map"===i||"Set"===i?Array.from(e):"Arguments"===i||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(i)?_arrayLikeToArray(e,t):void 0}}function _arrayLikeToArray(e,t){(null==t||t>e.length)&&(t=e.length);for(var i=0,s=new Array(t);i<t;i++)s[i]=e[i];return s}function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function _defineProperties(e,t){for(var i=0;i<t.length;i++){var s=t[i];s.enumerable=s.enumerable||!1,s.configurable=!0,"value"in s&&(s.writable=!0),Object.defineProperty(e,s.key,s)}}function _createClass(e,t,i){return t&&_defineProperties(e.prototype,t),i&&_defineProperties(e,i),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[5],{"0OPF":function(e){e.exports=JSON.parse('[{"description":"Use <b>peer estimates</b> to determine the <b>work distribution percentage</b> among team members in <b>a team activity</b>","question":{"feedbackQuestionId":"","questionNumber":1,"questionBrief":"How much work did each team member contribute? (response will be shown anonymously to each team member).","questionDescription":"","questionType":"CONTRIB","questionDetails":{"isNotSureAllowed":false},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS_INCLUDING_SELF","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS","RECIPIENT","GIVER_TEAM_MEMBERS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS","RECIPIENT"]}},{"description":"Ask each student to describe something related to themselves i.e., <b>self-reflection</b>","question":{"feedbackQuestionId":"","questionNumber":2,"questionBrief":"What contributions did you make to the team? (response will be shown to each team member).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"SELF","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"],"showGiverNameTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"],"showRecipientNameTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"]}},{"description":"Ask each student to give <b>confidential peer feedback (qualitative)</b> to other <b>team members</b>","question":{"feedbackQuestionId":"","questionNumber":3,"questionBrief":"What comments do you have regarding each of your team members? (response is confidential and will only be shown to the instructor).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS"]}},{"description":"Ask each student to give <b>confidential feedback (qualitative)</b> about the team behaviour","question":{"feedbackQuestionId":"","questionNumber":4,"questionBrief":"How are the team dynamics thus far? (response is confidential and will only be to the instructor).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS"]}},{"description":"Ask each student to give <b>comments about other team members, in confidence</b>","question":{"feedbackQuestionId":"","questionNumber":5,"questionBrief":"What feedback do you have for each of your team members? (response will be shown anonymously to each team member).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["RECIPIENT","INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["RECIPIENT","INSTRUCTORS"]}}]')},Babz:function(e,t,i){"use strict";i.d(t,"a",(function(){return a}));var s=i("fXoL"),n=i("ofXK");function o(e,t){if(1&e){var i=s.Xb();s.Wb(0,"div",2),s.Wb(1,"p",3),s.Qc(2),s.Vb(),s.Wb(3,"button",4),s.ic("click",(function(){return s.Fc(i),s.kc().retryEvent.emit()})),s.Rb(4,"i",5),s.Qc(5,"\xa0Retry"),s.Vb(),s.Vb()}if(2&e){var n=s.kc();s.Cb(2),s.Rc(n.message)}}function r(e,t){1&e&&s.qc(0)}var T=["*"],a=function(){var e=function(){function e(){_classCallCheck(this,e),this.message="",this.shouldShowRetry=!1,this.retryEvent=new s.n}return _createClass(e,[{key:"ngOnInit",value:function(){}}]),e}();return e.\u0275fac=function(t){return new(t||e)},e.\u0275cmp=s.Kb({type:e,selectors:[["tm-loading-retry"]],inputs:{message:"message",shouldShowRetry:"shouldShowRetry"},outputs:{retryEvent:"retryEvent"},ngContentSelectors:T,decls:3,vars:2,consts:[["class","text-center",4,"ngIf","ngIfElse"],["content",""],[1,"text-center"],[1,"text-muted","m-1"],[1,"btn","btn-primary",3,"click"],["aria-hidden","true",1,"fas","fa-redo"]],template:function(e,t){if(1&e&&(s.rc(),s.Oc(0,o,6,1,"div",0),s.Oc(1,r,1,0,"ng-template",null,1,s.Pc)),2&e){var i=s.Cc(2);s.sc("ngIf",t.shouldShowRetry)("ngIfElse",i)}},directives:[n.u],styles:[""]}),e}()},Wsbs:function(e,t,i){"use strict";i.d(t,"a",(function(){return n}));var s=i("fXoL"),n=function(){var e=function(){function e(){_classCallCheck(this,e),this.useBlueSpinner=!1}return _createClass(e,[{key:"ngOnInit",value:function(){}}]),e}();return e.\u0275fac=function(t){return new(t||e)},e.\u0275cmp=s.Kb({type:e,selectors:[["tm-ajax-loading"]],inputs:{useBlueSpinner:"useBlueSpinner"},decls:2,vars:3,consts:[[1,"loading-container"],["role","status"]],template:function(e,t){1&e&&(s.Wb(0,"div",0),s.Rb(1,"div",1),s.Vb()),2&e&&(s.Cb(1),s.Fb("spinner-border spinner-border-sm text-",t.useBlueSpinner?"primary":"white",""))},styles:[".loading-container[_ngcontent-%COMP%]{display:inline;margin-right:10px}"]}),e}()},YxrJ:function(e,t,i){"use strict";i.d(t,"a",(function(){return R}));var s,n=i("0OPF"),o=i("vMQM"),r=i("R8lv"),T=i("q1I8"),a=i("RmXC"),c=i("7qkN"),E=function(){function e(t,i){_classCallCheck(this,e),this.visibility=new Map,this.editability=new Map,this.applicability=new Set,this.reset(),this.startFromNewState(t,i)}return _createClass(e,[{key:"reset",value:function(){this.visibility.clear(),this.editability.clear();for(var e=0,t=Object.keys(r.g);e<t.length;e++){var i=t[e],s=r.g[i];this.visibility.set(s,new Map),this.editability.set(s,new Map),this.applicability.add(s);for(var n=0,o=Object.keys(c.a);n<o.length;n++){var T=o[n],a=c.a[T];this.visibility.get(s).set(a,!1),this.editability.get(s).set(a,!0)}}this.editability.get(r.g.RECIPIENT).set(c.a.SHOW_RECIPIENT_NAME,!1)}},{key:"resetVisibility",value:function(){for(var e=0,t=Array.from(this.visibility.keys());e<t.length;e++)for(var i=t[e],s=0,n=Array.from(this.visibility.get(i).keys());s<n.length;s++){var o=n[s];this.disallowToSee(i,o)}}},{key:"startFromNewState",value:function(e,t){switch(this.reset(),e){case r.c.STUDENTS:break;case r.c.SELF:case r.c.INSTRUCTORS:case r.c.TEAMS:this.applicability.delete(r.g.GIVER_TEAM_MEMBERS);break;default:throw new Error("Unexpected giverType")}switch(t){case r.c.SELF:this.applicability.delete(r.g.RECIPIENT),this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS);break;case r.c.STUDENTS:break;case r.c.OWN_TEAM:this.applicability.delete(r.g.RECIPIENT),this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS);break;case r.c.INSTRUCTORS:case r.c.TEAMS:this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS);break;case r.c.OWN_TEAM_MEMBERS:case r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF:this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS);break;case r.c.NONE:this.applicability.delete(r.g.RECIPIENT),this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS);break;default:throw new Error("Unexpected recipientType")}e!==r.c.SELF&&e!==r.c.INSTRUCTORS||t!==r.c.SELF||this.applicability.delete(r.g.RECIPIENT_TEAM_MEMBERS),e===r.c.TEAMS&&t===r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF&&this.applicability.delete(r.g.RECIPIENT)}},{key:"applyVisibilitySettings",value:function(e){this.resetVisibility();var t,i=_createForOfIteratorHelper(e.SHOW_RESPONSE);try{for(i.s();!(t=i.n()).done;){var s=t.value;this.allowToSee(s,c.a.SHOW_RESPONSE)}}catch(S){i.e(S)}finally{i.f()}var n,o=_createForOfIteratorHelper(e.SHOW_GIVER_NAME);try{for(o.s();!(n=o.n()).done;){var r=n.value;this.allowToSee(r,c.a.SHOW_GIVER_NAME)}}catch(S){o.e(S)}finally{o.f()}var T,a=_createForOfIteratorHelper(e.SHOW_RECIPIENT_NAME);try{for(a.s();!(T=a.n()).done;){var E=T.value;this.allowToSee(E,c.a.SHOW_RECIPIENT_NAME)}}catch(S){a.e(S)}finally{a.f()}}},{key:"allowToSee",value:function(e,t){if(this.isCellEditable(e,t)&&this.isVisibilityTypeApplicable(e))switch(this.visibility.get(e).set(t,!0),t){case c.a.SHOW_RESPONSE:e===r.g.RECIPIENT&&this.visibility.get(e).set(c.a.SHOW_RECIPIENT_NAME,!0);break;case c.a.SHOW_GIVER_NAME:case c.a.SHOW_RECIPIENT_NAME:this.visibility.get(e).set(c.a.SHOW_RESPONSE,!0)}}},{key:"disallowToSee",value:function(e,t){if(this.isCellEditable(e,t)&&this.isVisibilityTypeApplicable(e))switch(this.visibility.get(e).set(t,!1),t){case c.a.SHOW_RESPONSE:this.visibility.get(e).set(c.a.SHOW_GIVER_NAME,!1),this.visibility.get(e).set(c.a.SHOW_RECIPIENT_NAME,!1)}}},{key:"isVisibilityTypeApplicable",value:function(e){return this.applicability.has(e)}},{key:"hasAnyVisibilityControl",value:function(e){return Array.from(this.visibility.get(e).values()).some((function(e){return e}))}},{key:"hasAnyVisibilityControlForAll",value:function(){for(var e=0,t=Object.keys(r.g);e<t.length;e++){var i=t[e];if(this.hasAnyVisibilityControl(r.g[i]))return!0}return!1}},{key:"isCellEditable",value:function(e,t){return this.editability.get(e).get(t)}},{key:"isVisible",value:function(e,t){return this.visibility.get(e).get(t)}},{key:"getVisibilityTypesUnderVisibilityControl",value:function(e){for(var t=[],i=0,s=Object.keys(r.g);i<s.length;i++){var n=s[i],o=r.g[n];this.isVisible(o,e)&&t.push(o)}return t}},{key:"getVisibilityControlUnderVisibilityType",value:function(e){return{SHOW_RESPONSE:this.isVisible(e,c.a.SHOW_RESPONSE),SHOW_GIVER_NAME:this.isVisible(e,c.a.SHOW_GIVER_NAME),SHOW_RECIPIENT_NAME:this.isVisible(e,c.a.SHOW_RECIPIENT_NAME)}}}]),e}(),S=i("fXoL"),N=i("mW1j"),R=((s=function(){function e(t){_classCallCheck(this,e),this.httpRequestService=t}return _createClass(e,[{key:"getAllowedFeedbackPaths",value:function(e){var t=new Map;switch(e){case r.d.CONTRIB:t.set(r.c.STUDENTS,[r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]);break;case r.d.RANK_RECIPIENTS:case r.d.CONSTSUM_RECIPIENTS:t.set(r.c.SELF,[r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS]),t.set(r.c.STUDENTS,[r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM_MEMBERS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]),t.set(r.c.INSTRUCTORS,[r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS]),t.set(r.c.TEAMS,[r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]);break;case r.d.TEXT:case r.d.MCQ:case r.d.MSQ:case r.d.NUMSCALE:case r.d.RANK_OPTIONS:case r.d.RUBRIC:case r.d.CONSTSUM_OPTIONS:t.set(r.c.SELF,[r.c.SELF,r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM,r.c.NONE]),t.set(r.c.STUDENTS,[r.c.SELF,r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM,r.c.OWN_TEAM_MEMBERS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF,r.c.NONE]),t.set(r.c.INSTRUCTORS,[r.c.SELF,r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM,r.c.NONE]),t.set(r.c.TEAMS,[r.c.SELF,r.c.STUDENTS,r.c.INSTRUCTORS,r.c.TEAMS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF,r.c.NONE])}return t}},{key:"getCommonFeedbackPaths",value:function(e){var t=new Map;switch(e){case r.d.CONTRIB:t.set(r.c.STUDENTS,[r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]);break;case r.d.RANK_RECIPIENTS:case r.d.CONSTSUM_RECIPIENTS:t.set(r.c.SELF,[r.c.INSTRUCTORS]),t.set(r.c.STUDENTS,[r.c.INSTRUCTORS,r.c.OWN_TEAM_MEMBERS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]),t.set(r.c.INSTRUCTORS,[r.c.INSTRUCTORS]);break;case r.d.TEXT:case r.d.MCQ:case r.d.MSQ:case r.d.NUMSCALE:case r.d.RANK_OPTIONS:case r.d.RUBRIC:case r.d.CONSTSUM_OPTIONS:t.set(r.c.SELF,[r.c.NONE,r.c.SELF,r.c.INSTRUCTORS]),t.set(r.c.STUDENTS,[r.c.NONE,r.c.SELF,r.c.INSTRUCTORS,r.c.OWN_TEAM_MEMBERS,r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF]),t.set(r.c.INSTRUCTORS,[r.c.NONE,r.c.SELF,r.c.INSTRUCTORS])}return t}},{key:"getNewVisibilityStateMachine",value:function(e,t){return new E(e,t)}},{key:"getCommonFeedbackVisibilitySettings",value:function(e,t){var i=[];switch(t){case r.d.CONTRIB:i.push({name:"Shown anonymously to recipient and giver's team members, visible to instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}},{name:"Visible to instructors only",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS]}});break;case r.d.TEXT:case r.d.MCQ:case r.d.MSQ:case r.d.NUMSCALE:case r.d.RANK_OPTIONS:case r.d.RANK_RECIPIENTS:case r.d.RUBRIC:case r.d.CONSTSUM_OPTIONS:case r.d.CONSTSUM_RECIPIENTS:i.push({name:"Shown anonymously to recipient and instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT],SHOW_GIVER_NAME:[],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}},{name:"Shown anonymously to recipient, visible to instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}},{name:"Shown anonymously to recipient and giver/recipient's team members, visible to instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS,r.g.RECIPIENT_TEAM_MEMBERS],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}},{name:"Shown anonymously to recipient and giver's team members, visible to instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}},{name:"Visible to instructors only",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS],SHOW_GIVER_NAME:[r.g.INSTRUCTORS],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS]}},{name:"Visible to recipient and instructors",visibilitySettings:{SHOW_RESPONSE:[r.g.INSTRUCTORS,r.g.RECIPIENT],SHOW_GIVER_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT],SHOW_RECIPIENT_NAME:[r.g.INSTRUCTORS,r.g.RECIPIENT]}})}return i=i.filter((function(t){var i,s=_createForOfIteratorHelper(t.visibilitySettings.SHOW_RESPONSE);try{for(s.s();!(i=s.n()).done;){var n=i.value;if(!e.isVisibilityTypeApplicable(n))return!1}}catch(S){s.e(S)}finally{s.f()}var o,r=_createForOfIteratorHelper(t.visibilitySettings.SHOW_GIVER_NAME);try{for(r.s();!(o=r.n()).done;){var T=o.value;if(!e.isVisibilityTypeApplicable(T))return!1}}catch(S){r.e(S)}finally{r.f()}var a,c=_createForOfIteratorHelper(t.visibilitySettings.SHOW_RECIPIENT_NAME);try{for(c.s();!(a=c.n()).done;){var E=a.value;if(!e.isVisibilityTypeApplicable(E))return!1}}catch(S){c.e(S)}finally{c.f()}return!0}))}},{key:"isCustomFeedbackVisibilitySettingAllowed",value:function(e){switch(e){case r.d.TEXT:return!0;case r.d.CONTRIB:return!1;case r.d.MCQ:case r.d.NUMSCALE:case r.d.MSQ:case r.d.RANK_OPTIONS:case r.d.RANK_RECIPIENTS:return!0;case r.d.RUBRIC:case r.d.CONSTSUM_OPTIONS:case r.d.CONSTSUM_RECIPIENTS:return!0;default:throw new Error("Unsupported question type: "+e)}}},{key:"getNewQuestionModel",value:function(e){switch(e){case r.d.TEXT:return{questionBrief:"",questionDescription:"",questionType:r.d.TEXT,questionDetails:Object(T.r)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.CONTRIB:return{questionBrief:"",questionDescription:"",questionType:r.d.CONTRIB,questionDetails:Object(T.d)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS_INCLUDING_SELF,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.NUMSCALE:return{questionBrief:"",questionDescription:"",questionType:r.d.NUMSCALE,questionDetails:Object(T.j)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.MCQ:var t=Object(T.f)();return t.numOfMcqChoices=2,t.mcqChoices=["",""],{questionBrief:"",questionDescription:"",questionType:r.d.MCQ,questionDetails:t,giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.MSQ:var i=Object(T.h)();return i.msqChoices=["",""],i.minSelectableChoices=a.f,i.maxSelectableChoices=a.f,{questionBrief:"",questionDescription:"",questionType:r.d.MSQ,questionDetails:i,giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.RANK_OPTIONS:var s=Object(T.l)();return s.maxOptionsToBeRanked=a.f,s.minOptionsToBeRanked=a.f,s.options=["",""],{questionBrief:"",questionDescription:"",questionType:r.d.RANK_OPTIONS,questionDetails:s,giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT,r.g.GIVER_TEAM_MEMBERS],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.RANK_RECIPIENTS:return{questionBrief:"",questionDescription:"",questionType:r.d.RANK_RECIPIENTS,questionDetails:Object(T.n)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.RUBRIC:var n=Object(T.p)();return n.numOfRubricChoices=4,n.rubricChoices=["Strongly Disagree","Disagree","Agree","Strongly Agree"],n.numOfRubricSubQuestions=2,n.rubricSubQuestions=["This student participates well in online discussions.","This student completes assigned tasks on time."],n.rubricDescriptions=[["Rarely or never responds.","Occasionally responds, but never initiates discussions.","Takes part in discussions and sometimes initiates discussions.","Initiates discussions frequently, and engages the team."],["Rarely or never completes tasks.","Often misses deadlines.","Occasionally misses deadlines.","Tasks are always completed before the deadline."]],{questionBrief:"",questionDescription:"",questionType:r.d.RUBRIC,questionDetails:n,giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.CONSTSUM_OPTIONS:return{questionBrief:"",questionDescription:"",questionType:r.d.CONSTSUM_OPTIONS,questionDetails:Object(T.a)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};case r.d.CONSTSUM_RECIPIENTS:return{questionBrief:"",questionDescription:"",questionType:r.d.CONSTSUM_RECIPIENTS,questionDetails:Object(T.b)(),giverType:r.c.STUDENTS,recipientType:r.c.OWN_TEAM_MEMBERS,numberOfEntitiesToGiveFeedbackToSetting:r.m.UNLIMITED,showResponsesTo:[r.g.INSTRUCTORS,r.g.RECIPIENT],showGiverNameTo:[r.g.INSTRUCTORS],showRecipientNameTo:[r.g.INSTRUCTORS,r.g.RECIPIENT]};default:throw new Error("Unsupported question type "+e)}}},{key:"getFeedbackQuestions",value:function(e){var t={intent:e.intent,courseid:e.courseId,fsname:e.feedbackSessionName};return e.key&&(t.key=e.key),e.moderatedPerson&&(t.moderatedperson=e.moderatedPerson),e.previewAs&&(t.previewas=e.previewAs),this.httpRequestService.get(o.d.QUESTIONS,t)}},{key:"isAllowedToHaveParticipantComment",value:function(e){return e===r.d.MCQ}},{key:"getTemplateQuestions",value:function(){return n}},{key:"createFeedbackQuestion",value:function(e,t,i){return this.httpRequestService.post(o.d.QUESTION,{courseid:e,fsname:t},i)}},{key:"saveFeedbackQuestion",value:function(e,t){return this.httpRequestService.put(o.d.QUESTION,{questionid:e},t)}},{key:"deleteFeedbackQuestion",value:function(e){return this.httpRequestService.delete(o.d.QUESTION,{questionid:e})}},{key:"loadFeedbackQuestionRecipients",value:function(e){return this.httpRequestService.get(o.d.QUESTION_RECIPIENTS,{questionid:e.questionId,intent:e.intent,key:e.key,moderatedperson:e.moderatedPerson,previewas:e.previewAs})}}]),e}()).\u0275fac=function(e){return new(e||s)(S.ec(N.a))},s.\u0275prov=S.Mb({token:s,factory:s.\u0275fac,providedIn:"root"}),s)},mYbV:function(e,t,i){"use strict";i.d(t,"a",(function(){return o}));var s=i("ofXK"),n=i("fXoL"),o=function(){var e=function e(){_classCallCheck(this,e)};return e.\u0275mod=n.Ob({type:e}),e.\u0275inj=n.Nb({factory:function(t){return new(t||e)},imports:[[s.c]]}),e}()}}]);