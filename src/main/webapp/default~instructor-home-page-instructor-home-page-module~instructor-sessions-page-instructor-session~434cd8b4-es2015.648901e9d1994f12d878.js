(window.webpackJsonp=window.webpackJsonp||[]).push([[15],{"4mYk":function(e,s,n){"use strict";n.d(s,"a",(function(){return i}));var t=n("fXoL"),o=n("A3DQ");let i=(()=>{class e{constructor(e){this.timezoneService=e}transform(e,s){return this.timezoneService.formatToString(e,s,"ddd, DD MMM YYYY, HH:mm A z")}}return e.\u0275fac=function(s){return new(s||e)(t.Qb(o.b))},e.\u0275pipe=t.Pb({name:"formatDateDetail",type:e,pure:!0}),e})()},BKNR:function(e,s,n){"use strict";n.d(s,"a",(function(){return t})),n.d(s,"b",(function(){return o}));var t=function(e){return e[e.COURSE_ID=0]="COURSE_ID",e[e.START_DATE=1]="START_DATE",e[e.END_DATE=2]="END_DATE",e}({}),o=function(e){return e[e.BLUE=0]="BLUE",e[e.WHITE=1]="WHITE",e}({})},FYy5:function(e,s,n){"use strict";n.d(s,"a",(function(){return i}));var t=n("R8lv"),o=n("fXoL");let i=(()=>{class e{transform(e){switch(e){case t.f.NOT_VISIBLE:case t.f.VISIBLE_NOT_OPEN:return"Awaiting";case t.f.OPEN:case t.f.GRACE_PERIOD:return"Open";case t.f.CLOSED:return"Closed";default:return"Unknown"}}}return e.\u0275fac=function(s){return new(s||e)},e.\u0275pipe=o.Pb({name:"submissionStatusName",type:e,pure:!0}),e})()},H5dr:function(e,s,n){"use strict";n.d(s,"a",(function(){return ee}));var t=n("fXoL"),o=n("R8lv"),i=n("p6cw"),c=n("84gB"),a=n("/Tzu"),r=n("BKNR"),b=n("1kSV"),l=n("eQqG"),d=n("ofXK"),u=n("W872"),S=n("Wsbs");let f=(()=>{class e{transform(e){let s="The feedback session has been created";switch(e){case o.f.VISIBLE_NOT_OPEN:case o.f.OPEN:case o.f.GRACE_PERIOD:case o.f.CLOSED:s+=", is visible"}switch(e){case o.f.VISIBLE_NOT_OPEN:s+=", and is waiting to open";break;case o.f.OPEN:s+=", and is open for submissions";break;case o.f.CLOSED:s+=", and has ended"}return s+=".",s}}return e.\u0275fac=function(s){return new(s||e)},e.\u0275pipe=t.Pb({name:"submissionStatusTooltip",type:e,pure:!0}),e})();var m=n("FYy5");let p=(()=>{class e{transform(e){switch(e){case o.e.PUBLISHED:return"The responses for this session are visible.";case o.e.NOT_PUBLISHED:return"The responses for this session are not visible.";default:return"Unknown"}}}return e.\u0275fac=function(s){return new(s||e)},e.\u0275pipe=t.Pb({name:"publishStatusTooltip",type:e,pure:!0}),e})();var h=n("pTm8"),k=n("4mYk"),R=n("WPPJ");function T(e,s){1&e&&t.Rb(0,"i",17)}function g(e,s){1&e&&t.Rb(0,"i",18)}function w(e,s){if(1&e){const e=t.Xb();t.Wb(0,"th",16),t.ic("click",(function(){t.Fc(e);const s=t.kc(2);return s.sortSessionsTableRowModels(s.SortBy.COURSE_ID)})),t.Qc(1," Course ID "),t.Wb(2,"span",8),t.Rb(3,"i",9),t.Oc(4,T,1,0,"i",10),t.Oc(5,g,1,0,"i",11),t.Vb(),t.Vb()}if(2&e){const e=t.kc(2);t.Cb(4),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.COURSE_ID&&e.sessionsTableRowModelsSortOrder===e.SortOrder.DESC),t.Cb(1),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.COURSE_ID&&e.sessionsTableRowModelsSortOrder===e.SortOrder.ASC)}}function I(e,s){1&e&&t.Rb(0,"i",17)}function E(e,s){1&e&&t.Rb(0,"i",18)}function v(e,s){1&e&&t.Rb(0,"i",17)}function C(e,s){1&e&&t.Rb(0,"i",18)}function O(e,s){if(1&e){const e=t.Xb();t.Wb(0,"th",19),t.ic("click",(function(){t.Fc(e);const s=t.kc(2);return s.sortSessionsTableRowModels(s.SortBy.SESSION_START_DATE)})),t.Qc(1," Start Date "),t.Wb(2,"span",8),t.Rb(3,"i",9),t.Oc(4,v,1,0,"i",10),t.Oc(5,C,1,0,"i",11),t.Vb(),t.Vb()}if(2&e){const e=t.kc(2);t.Cb(4),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_START_DATE&&e.sessionsTableRowModelsSortOrder===e.SortOrder.DESC),t.Cb(1),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_START_DATE&&e.sessionsTableRowModelsSortOrder===e.SortOrder.ASC)}}function M(e,s){1&e&&t.Rb(0,"i",17)}function y(e,s){1&e&&t.Rb(0,"i",18)}function N(e,s){if(1&e){const e=t.Xb();t.Wb(0,"th",19),t.ic("click",(function(){t.Fc(e);const s=t.kc(2);return s.sortSessionsTableRowModels(s.SortBy.SESSION_END_DATE)})),t.Qc(1," End Date "),t.Wb(2,"span",8),t.Rb(3,"i",9),t.Oc(4,M,1,0,"i",10),t.Oc(5,y,1,0,"i",11),t.Vb(),t.Vb()}if(2&e){const e=t.kc(2);t.Cb(4),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_END_DATE&&e.sessionsTableRowModelsSortOrder===e.SortOrder.DESC),t.Cb(1),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_END_DATE&&e.sessionsTableRowModelsSortOrder===e.SortOrder.ASC)}}function L(e,s){if(1&e&&(t.Wb(0,"td"),t.Qc(1),t.Vb()),2&e){const e=t.kc().$implicit;t.Cb(1),t.Rc(e.feedbackSession.courseId)}}function V(e,s){if(1&e&&(t.Wb(0,"td"),t.Wb(1,"span",21),t.lc(2,"formatDateDetail"),t.Qc(3),t.lc(4,"formatDateBrief"),t.Vb(),t.Vb()),2&e){const e=t.kc().$implicit;t.Cb(1),t.sc("ngbTooltip",t.nc(2,2,e.feedbackSession.submissionStartTimestamp,e.feedbackSession.timeZone)),t.Cb(2),t.Rc(t.nc(4,5,e.feedbackSession.submissionStartTimestamp,e.feedbackSession.timeZone))}}function D(e,s){if(1&e&&(t.Wb(0,"td"),t.Wb(1,"span",21),t.lc(2,"formatDateDetail"),t.Qc(3),t.lc(4,"formatDateBrief"),t.Vb(),t.Vb()),2&e){const e=t.kc().$implicit;t.Cb(1),t.sc("ngbTooltip",t.nc(2,2,e.feedbackSession.submissionEndTimestamp,e.feedbackSession.timeZone)),t.Cb(2),t.Rc(t.nc(4,5,e.feedbackSession.submissionEndTimestamp,e.feedbackSession.timeZone))}}function W(e,s){if(1&e){const e=t.Xb();t.Wb(0,"a",42),t.ic("click",(function(s){t.Fc(e);const n=t.kc().index;return t.kc(2).loadResponseRateEvent.emit(n),s.preventDefault()})),t.Qc(1,"Show"),t.Vb()}if(2&e){const e=t.kc().index;t.uc("id","show-response-rate-",e,"")}}function P(e,s){if(1&e&&(t.Wb(0,"div",43),t.Qc(1),t.Vb()),2&e){const e=t.kc(),s=e.$implicit;t.uc("id","response-rate-",e.index,""),t.Cb(1),t.Rc(s.responseRate)}}function B(e,s){1&e&&t.Rb(0,"tm-ajax-loading",44),2&e&&t.sc("useBlueSpinner",!0)}function _(e,s){1&e&&t.Sb(0)}const A=function(e,s){return{courseid:e,fsname:s,editingMode:!0}};function F(e,s){if(1&e&&(t.Wb(0,"a",45),t.Oc(1,_,1,0,"ng-container",46),t.Vb()),2&e){const e=t.kc().$implicit,s=t.Cc(23);t.sc("queryParams",t.xc(2,A,e.feedbackSession.courseId,e.feedbackSession.feedbackSessionName)),t.Cb(1),t.sc("ngTemplateOutlet",s)}}function Q(e,s){if(1&e){const e=t.Xb();t.Wb(0,"button",47),t.ic("click",(function(){t.Fc(e);const s=t.kc().index;return t.kc(2).editSessionEvent.emit(s)})),t.Qc(1," Edit "),t.Vb()}if(2&e){const e=t.kc().$implicit;t.sc("disabled",!e.instructorPrivilege.canModifySession)}}function x(e,s){1&e&&t.Sb(0)}const U=function(e,s){return{courseid:e,fsname:s}};function X(e,s){if(1&e&&(t.Wb(0,"a",48),t.Oc(1,x,1,0,"ng-container",46),t.Vb()),2&e){const e=t.kc().$implicit,s=t.Cc(30);t.sc("queryParams",t.xc(2,U,e.feedbackSession.courseId,e.feedbackSession.feedbackSessionName)),t.Cb(1),t.sc("ngTemplateOutlet",s)}}function j(e,s){if(1&e){const e=t.Xb();t.Wb(0,"button",49),t.ic("click",(function(){t.Fc(e);const s=t.kc().index;return t.kc(2).submitSessionAsInstructorEvent.emit(s)})),t.Qc(1,"Submit"),t.Vb()}if(2&e){const e=t.kc().$implicit,s=t.kc(2);t.sc("disabled",e.feedbackSession.submissionStatus!==s.FeedbackSessionSubmissionStatus.OPEN||!e.instructorPrivilege.canSubmitSessionInSections)}}function H(e,s){if(1&e){const e=t.Xb();t.Wb(0,"button",50),t.ic("click",(function(){t.Fc(e);const s=t.kc().index;return t.kc(2).unpublishSession(s)})),t.Qc(1,"Unpublish Results"),t.Vb()}if(2&e){const e=t.kc(),s=e.$implicit;t.uc("id","btn-unpublish-",e.index,""),t.sc("disabled",!s.instructorPrivilege.canModifySession)}}const $=function(e,s){return[e,s]};function q(e,s){if(1&e){const e=t.Xb();t.Wb(0,"button",51),t.ic("click",(function(){t.Fc(e);const s=t.kc().index;return t.kc(2).publishSession(s)})),t.Qc(1,"Publish Results"),t.Vb()}if(2&e){const e=t.kc(),s=e.index,n=e.$implicit,o=t.kc(2);t.uc("id","btn-publish-",s,""),t.sc("disabled",t.xc(2,$,o.FeedbackSessionSubmissionStatus.NOT_VISIBLE,o.FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN).includes(n.feedbackSession.submissionStatus)||n.feedbackSession.publishStatus===o.FeedbackSessionPublishStatus.PUBLISHED||!n.instructorPrivilege.canModifySession)}}function Y(e,s){if(1&e){const e=t.Xb();t.Wb(0,"button",52),t.ic("click",(function(){t.Fc(e);const s=t.kc().index;return t.kc(2).remindResultsLinkToStudent(s)})),t.Qc(1,"Resend link to view results"),t.Vb()}if(2&e){const e=t.kc().index;t.uc("id","btn-resend-",e,"")}}const z=function(e){return{disabled:e}};function G(e,s){if(1&e){const e=t.Xb();t.Wb(0,"tr"),t.Oc(1,L,2,1,"td",20),t.Wb(2,"td"),t.Qc(3),t.Vb(),t.Oc(4,V,5,8,"td",20),t.Oc(5,D,5,8,"td",20),t.Wb(6,"td"),t.Wb(7,"span",21),t.lc(8,"submissionStatusTooltip"),t.Qc(9),t.lc(10,"submissionStatusName"),t.Vb(),t.Vb(),t.Wb(11,"td"),t.Wb(12,"span",21),t.lc(13,"publishStatusTooltip"),t.Qc(14),t.lc(15,"publishStatusName"),t.Vb(),t.Vb(),t.Wb(16,"td"),t.Oc(17,W,2,1,"a",22),t.Oc(18,P,2,2,"div",23),t.Oc(19,B,1,1,"tm-ajax-loading",24),t.Vb(),t.Wb(20,"td",25),t.Oc(21,F,2,5,"a",26),t.Oc(22,Q,2,1,"ng-template",null,27,t.Pc),t.Wb(24,"button",28),t.ic("click",(function(){t.Fc(e);const n=s.index;return t.kc(2).moveSessionToRecycleBin(n)})),t.Qc(25,"Delete"),t.Vb(),t.Wb(26,"button",29),t.ic("click",(function(){t.Fc(e);const n=s.index;return t.kc(2).copySession(n)})),t.Qc(27,"Copy"),t.Vb(),t.Oc(28,X,2,5,"a",30),t.Oc(29,j,2,1,"ng-template",null,31,t.Pc),t.Wb(31,"div",32),t.Wb(32,"button",33),t.Qc(33,"Results"),t.Vb(),t.Wb(34,"div",34),t.Wb(35,"a",35),t.ic("click",(function(){t.Fc(e);const n=s.index;return t.kc(2).viewSessionResultEvent.emit(n)})),t.Qc(36,"View Results"),t.Vb(),t.Oc(37,H,2,2,"button",36),t.Oc(38,q,2,5,"ng-template",null,37,t.Pc),t.Oc(40,Y,2,1,"button",38),t.Wb(41,"button",39),t.ic("click",(function(){t.Fc(e);const n=s.index;return t.kc(2).downloadSessionResults(n)})),t.Qc(42,"Download Results"),t.Vb(),t.Vb(),t.Vb(),t.Wb(43,"div",40),t.Wb(44,"button",41),t.ic("click",(function(){t.Fc(e);const n=s.index;return t.kc(2).sendRemindersToStudents(n)})),t.Qc(45,"Remind"),t.Vb(),t.Vb(),t.Vb(),t.Vb()}if(2&e){const e=s.$implicit,n=s.index,o=t.Cc(23),i=t.Cc(30),c=t.Cc(39),a=t.kc(2);t.Cb(1),t.sc("ngIf",a.columnsToShow.includes(a.SessionsTableColumn.COURSE_ID)),t.Cb(2),t.Rc(e.feedbackSession.feedbackSessionName),t.Cb(1),t.sc("ngIf",a.columnsToShow.includes(a.SessionsTableColumn.START_DATE)),t.Cb(1),t.sc("ngIf",a.columnsToShow.includes(a.SessionsTableColumn.END_DATE)),t.Cb(2),t.sc("ngbTooltip",t.mc(8,28,e.feedbackSession.submissionStatus)),t.Cb(2),t.Rc(t.mc(10,30,e.feedbackSession.submissionStatus)),t.Cb(3),t.sc("ngbTooltip",t.mc(13,32,e.feedbackSession.publishStatus)),t.Cb(2),t.Rc(t.mc(15,34,e.feedbackSession.publishStatus)),t.Cb(3),t.sc("ngIf",0===e.responseRate.length&&!e.isLoadingResponseRate),t.Cb(1),t.sc("ngIf",0!==e.responseRate.length),t.Cb(1),t.sc("ngIf",e.isLoadingResponseRate),t.Cb(2),t.sc("ngIf",e.instructorPrivilege.canModifySession)("ngIfElse",o),t.Cb(3),t.uc("id","btn-soft-delete-",n,""),t.sc("disabled",!e.instructorPrivilege.canModifySession),t.Cb(2),t.uc("id","btn-copy-",n,""),t.Cb(2),t.sc("ngIf",e.feedbackSession.submissionStatus===a.FeedbackSessionSubmissionStatus.OPEN&&e.instructorPrivilege.canSubmitSessionInSections)("ngIfElse",i),t.Cb(4),t.uc("id","btn-results-",n,""),t.Cb(3),t.sc("ngClass",t.wc(36,z,!e.instructorPrivilege.canViewSessionInSections))("queryParams",t.xc(38,U,e.feedbackSession.courseId,e.feedbackSession.feedbackSessionName)),t.Cb(2),t.sc("ngIf",!t.xc(41,$,a.FeedbackSessionSubmissionStatus.NOT_VISIBLE,a.FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN).includes(e.feedbackSession.submissionStatus)&&e.feedbackSession.publishStatus===a.FeedbackSessionPublishStatus.PUBLISHED)("ngIfElse",c),t.Cb(3),t.sc("ngIf",e.feedbackSession.publishStatus===a.FeedbackSessionPublishStatus.PUBLISHED),t.Cb(1),t.uc("id","btn-download-",n,""),t.sc("disabled",!e.instructorPrivilege.canViewSessionInSections),t.Cb(3),t.uc("id","btn-remind-",n,""),t.sc("disabled",e.feedbackSession.submissionStatus!==a.FeedbackSessionSubmissionStatus.OPEN||!e.instructorPrivilege.canModifySession)}}const K=function(e){return{"bg-primary text-white":e}};function J(e,s){if(1&e){const e=t.Xb();t.Wb(0,"div",2),t.Wb(1,"div",3),t.Wb(2,"table",4),t.Wb(3,"thead"),t.Wb(4,"tr",5),t.Oc(5,w,6,2,"th",6),t.Wb(6,"th",7),t.ic("click",(function(){t.Fc(e);const s=t.kc();return s.sortSessionsTableRowModels(s.SortBy.SESSION_NAME)})),t.Qc(7," Session Name "),t.Wb(8,"span",8),t.Rb(9,"i",9),t.Oc(10,I,1,0,"i",10),t.Oc(11,E,1,0,"i",11),t.Vb(),t.Vb(),t.Oc(12,O,6,2,"th",12),t.Oc(13,N,6,2,"th",12),t.Wb(14,"th"),t.Qc(15,"Submissions"),t.Vb(),t.Wb(16,"th"),t.Qc(17,"Responses"),t.Vb(),t.Wb(18,"th",13),t.Wb(19,"span"),t.Qc(20,"Response Rate"),t.Vb(),t.Vb(),t.Wb(21,"th",14),t.Qc(22,"Action(s)"),t.Vb(),t.Vb(),t.Vb(),t.Wb(23,"tbody"),t.Oc(24,G,46,44,"tr",15),t.Vb(),t.Vb(),t.Vb(),t.Vb()}if(2&e){const e=t.kc();t.Cb(4),t.sc("ngClass",t.wc(7,K,e.headerColorScheme===e.SessionsTableHeaderColorScheme.BLUE)),t.Cb(1),t.sc("ngIf",e.columnsToShow.includes(e.SessionsTableColumn.COURSE_ID)),t.Cb(5),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_NAME&&e.sessionsTableRowModelsSortOrder===e.SortOrder.DESC),t.Cb(1),t.sc("ngIf",e.sessionsTableRowModelsSortBy===e.SortBy.SESSION_NAME&&e.sessionsTableRowModelsSortOrder===e.SortOrder.ASC),t.Cb(1),t.sc("ngIf",e.columnsToShow.includes(e.SessionsTableColumn.START_DATE)),t.Cb(1),t.sc("ngIf",e.columnsToShow.includes(e.SessionsTableColumn.END_DATE)),t.Cb(11),t.sc("ngForOf",e.sessionsTableRowModels)}}function Z(e,s){1&e&&(t.Wb(0,"div",53),t.Qc(1," There are no feedback sessions in this course. "),t.Vb())}let ee=(()=>{class e{constructor(e,s){this.ngbModal=e,this.simpleModalService=s,this.SortBy=i.a,this.SortOrder=i.b,this.SessionsTableColumn=r.a,this.FeedbackSessionSubmissionStatus=o.f,this.FeedbackSessionPublishStatus=o.e,this.SessionsTableHeaderColorScheme=r.b,this.sessionsTableRowModels=[],this.courseCandidates=[],this.columnsToShow=[r.a.COURSE_ID],this.sessionsTableRowModelsSortBy=i.a.NONE,this.sessionsTableRowModelsSortOrder=i.b.ASC,this.headerColorScheme=r.b.BLUE,this.sortSessionsTableRowModelsEvent=new t.n,this.loadResponseRateEvent=new t.n,this.editSessionEvent=new t.n,this.moveSessionToRecycleBinEvent=new t.n,this.copySessionEvent=new t.n,this.submitSessionAsInstructorEvent=new t.n,this.viewSessionResultEvent=new t.n,this.publishSessionEvent=new t.n,this.unpublishSessionEvent=new t.n,this.sendRemindersToStudentsEvent=new t.n,this.resendResultsLinkToStudentsEvent=new t.n,this.downloadSessionResultsEvent=new t.n}sortSessionsTableRowModels(e){this.sortSessionsTableRowModelsEvent.emit(e)}moveSessionToRecycleBin(e){this.simpleModalService.openConfirmationModal(`Delete session <strong>${this.sessionsTableRowModels[e].feedbackSession.feedbackSessionName}</strong>?`,a.a.WARNING,'Session will be moved to the recycle bin. This action can be reverted by going to the "Sessions" tab and restoring the desired session(s).').result.then(()=>{this.moveSessionToRecycleBinEvent.emit(e)},()=>{})}copySession(e){const s=this.ngbModal.open(c.a),n=this.sessionsTableRowModels[e];s.componentInstance.newFeedbackSessionName=n.feedbackSession.feedbackSessionName,s.componentInstance.courseCandidates=this.courseCandidates,s.componentInstance.sessionToCopyCourseId=n.feedbackSession.courseId,s.result.then(s=>{this.copySessionEvent.emit(Object.assign(Object.assign({},s),{sessionToCopyRowIndex:e}))},()=>{})}publishSession(e){this.simpleModalService.openConfirmationModal(`Publish session <strong>${this.sessionsTableRowModels[e].feedbackSession.feedbackSessionName}</strong>?`,a.a.WARNING,"An email will be sent to students to inform them that the responses are ready for viewing.").result.then(()=>{this.publishSessionEvent.emit(e)},()=>{})}unpublishSession(e){this.simpleModalService.openConfirmationModal(`Unpublish session <strong>${this.sessionsTableRowModels[e].feedbackSession.feedbackSessionName}</strong>?`,a.a.WARNING,"An email will be sent to students to inform them that the session has been unpublished\n        and the session responses will no longer be viewable by students.").result.then(()=>{this.unpublishSessionEvent.emit(e)},()=>{})}remindResultsLinkToStudent(e){this.resendResultsLinkToStudentsEvent.emit(e)}sendRemindersToStudents(e){this.sendRemindersToStudentsEvent.emit(e)}downloadSessionResults(e){this.downloadSessionResultsEvent.emit(e)}ngOnInit(){}}return e.\u0275fac=function(s){return new(s||e)(t.Qb(b.m),t.Qb(l.a))},e.\u0275cmp=t.Kb({type:e,selectors:[["tm-sessions-table"]],inputs:{sessionsTableRowModels:"sessionsTableRowModels",courseCandidates:"courseCandidates",columnsToShow:"columnsToShow",sessionsTableRowModelsSortBy:"sessionsTableRowModelsSortBy",sessionsTableRowModelsSortOrder:"sessionsTableRowModelsSortOrder",headerColorScheme:"headerColorScheme"},outputs:{sortSessionsTableRowModelsEvent:"sortSessionsTableRowModelsEvent",loadResponseRateEvent:"loadResponseRateEvent",editSessionEvent:"editSessionEvent",moveSessionToRecycleBinEvent:"moveSessionToRecycleBinEvent",copySessionEvent:"copySessionEvent",submitSessionAsInstructorEvent:"submitSessionAsInstructorEvent",viewSessionResultEvent:"viewSessionResultEvent",publishSessionEvent:"publishSessionEvent",unpublishSessionEvent:"unpublishSessionEvent",sendRemindersToStudentsEvent:"sendRemindersToStudentsEvent",resendResultsLinkToStudentsEvent:"resendResultsLinkToStudentsEvent",downloadSessionResultsEvent:"downloadSessionResultsEvent"},decls:3,vars:2,consts:[["class","row",4,"ngIf","ngIfElse"],["noSessionMessage",""],[1,"row"],[1,"col-12"],["id","sessions-table",1,"table","table-responsive-lg","table-striped","table-bordered","margin-bottom-0"],[3,"ngClass"],["id","sort-course-id","class","sortable-header",3,"click",4,"ngIf"],["id","sort-session-name",1,"sortable-header",3,"click"],[1,"fa-stack"],[1,"fas","fa-sort"],["class","fas fa-sort-down",4,"ngIf"],["class","fas fa-sort-up",4,"ngIf"],["class","sortable-header",3,"click",4,"ngIf"],["ngbTooltip","Number of students submitted / Class size","container","body",1,"ngb-tooltip-class"],[1,"text-center"],[4,"ngFor","ngForOf"],["id","sort-course-id",1,"sortable-header",3,"click"],[1,"fas","fa-sort-down"],[1,"fas","fa-sort-up"],[1,"sortable-header",3,"click"],[4,"ngIf"],[1,"ngb-tooltip-class",3,"ngbTooltip"],["href","#",3,"id","click",4,"ngIf"],[3,"id",4,"ngIf"],[3,"useBlueSpinner",4,"ngIf"],[1,"actions-cell"],["tmRouterLink","/web/instructor/sessions/edit",3,"queryParams",4,"ngIf","ngIfElse"],["editSessionBtn",""],["type","button",1,"btn","btn-light","btn-sm",3,"id","disabled","click"],["type","button","ngbTooltip","Copy feedback session details",1,"btn","btn-light","btn-sm",3,"id","click"],["tmRouterLink","/web/instructor/sessions/submission",3,"queryParams",4,"ngIf","ngIfElse"],["submitBtn",""],["ngbDropdown","",1,"d-inline-block"],["ngbDropdownToggle","",1,"btn","btn-light","btn-sm",3,"id"],["ngbDropdownMenu",""],["tmRouterLink","/web/instructor/sessions/result",1,"btn","dropdown-item","clickable",3,"ngClass","queryParams","click"],["class","btn dropdown-item clickable","ngbTooltip","Make responses no longer visible","placement","left","container","body",3,"id","disabled","click",4,"ngIf","ngIfElse"],["publishButton",""],["class","btn dropdown-item clickable",3,"id","click",4,"ngIf"],[1,"btn","dropdown-item","clickable",3,"id","disabled","click"],["ngbDropdown","","ngbTooltip","Send e-mails to remind students and instructors who have not submitted their feedbacks to do so",1,"d-inline-block"],[1,"btn","btn-light","btn-sm",3,"id","disabled","click"],["href","#",3,"id","click"],[3,"id"],[3,"useBlueSpinner"],["tmRouterLink","/web/instructor/sessions/edit",3,"queryParams"],[4,"ngTemplateOutlet"],["type","button",1,"btn","btn-light","btn-sm",3,"disabled","click"],["tmRouterLink","/web/instructor/sessions/submission",3,"queryParams"],["type","button","ngbTooltip","Start submitting feedback",1,"btn","btn-light","btn-sm",3,"disabled","click"],["ngbTooltip","Make responses no longer visible","placement","left","container","body",1,"btn","dropdown-item","clickable",3,"id","disabled","click"],["ngbTooltip","Make session responses available for viewing","placement","left","container","body",1,"btn","dropdown-item","clickable",3,"id","disabled","click"],[1,"btn","dropdown-item","clickable",3,"id","click"],[1,"no-session-message"]],template:function(e,s){if(1&e&&(t.Oc(0,J,25,9,"div",0),t.Oc(1,Z,2,0,"ng-template",null,1,t.Pc)),2&e){const e=t.Cc(2);t.sc("ngIf",s.sessionsTableRowModels.length>0)("ngIfElse",e)}},directives:[d.u,d.r,b.w,d.t,b.g,b.k,b.i,u.a,S.a,d.B],pipes:[f,m.a,p,h.a,k.a,R.a],styles:[".no-session-message[_ngcontent-%COMP%]{padding:10px;text-align:center}@media (min-width:1200px){.actions-cell[_ngcontent-%COMP%]{min-width:410px}}.actions-cell[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:not(.dropdown-item){margin-left:var(--btn-margin)}.clickable[_ngcontent-%COMP%], .sortable-header[_ngcontent-%COMP%]{cursor:pointer}.margin-bottom-0[_ngcontent-%COMP%]{margin-bottom:0}"]}),e})()},Mdjh:function(e,s,n){"use strict";n.d(s,"a",(function(){return t}));const t={canModifyCourse:!1,canModifySession:!1,canModifyStudent:!1,canSubmitSessionInSections:!1,canModifyInstructor:!1,canViewStudentInSections:!1,canModifySessionCommentsInSections:!1,canViewSessionInSections:!1}},TQ0J:function(e,s,n){"use strict";n.d(s,"a",(function(){return m}));var t=n("cp0P"),o=n("nYR2"),i=n("9jjE"),c=n("fXoL"),a=n("1kSV"),r=n("ofXK"),b=n("A4oe");function l(e,s){if(1&e){const e=c.Xb();c.Wb(0,"div",10),c.Wb(1,"tm-respondent-list-info-table",11),c.ic("studentListInfoTableRowModelsChange",(function(s){return c.Fc(e),c.kc().studentListInfoTableRowModels=s}))("instructorListInfoTableRowModelsChange",(function(s){return c.Fc(e),c.kc().instructorListInfoTableRowModels=s})),c.Vb(),c.Vb()}if(2&e){const e=c.kc();c.Cb(1),c.sc("studentListInfoTableRowModels",e.studentListInfoTableRowModels)("shouldDisplayHasSubmittedSessionColumn",!1)("instructorListInfoTableRowModels",e.instructorListInfoTableRowModels)}}function d(e,s){1&e&&(c.Wb(0,"h4",12),c.Qc(1," There are no participants to email. "),c.Vb())}let u=(()=>{class e{constructor(e){this.activeModal=e,this.courseId="",this.feedbackSessionName="",this.studentListInfoTableRowModels=[],this.instructorListInfoTableRowModels=[]}ngOnInit(){}collateRespondentsToSendHandler(){const e=this.studentListInfoTableRowModels.map(e=>Object.assign({},e)).filter(e=>e.isSelected),s=this.instructorListInfoTableRowModels.map(e=>Object.assign({},e)).filter(e=>e.isSelected);return e.concat(s)}}return e.\u0275fac=function(s){return new(s||e)(c.Qb(a.a))},e.\u0275cmp=c.Kb({type:e,selectors:[["tm-resend-results-link-to-respondent-modal"]],decls:16,vars:2,consts:[[1,"modal-header"],[1,"modal-title"],["type","button",1,"close",3,"click"],[1,"fas","fa-times"],[1,"modal-body"],["class","table-responsive",4,"ngIf","ngIfElse"],["noRespondentsToRemind",""],[1,"modal-footer"],["type","button",1,"btn",3,"click"],["id","btn-confirm-resend-results","type","button",1,"btn","btn-primary",3,"click"],[1,"table-responsive"],[3,"studentListInfoTableRowModels","shouldDisplayHasSubmittedSessionColumn","instructorListInfoTableRowModels","studentListInfoTableRowModelsChange","instructorListInfoTableRowModelsChange"],[1,"p-2","bg-info","text-white"]],template:function(e,s){if(1&e&&(c.Wb(0,"div",0),c.Wb(1,"h4",1),c.Qc(2," Resend Published Email "),c.Wb(3,"small"),c.Qc(4,"(Select the student(s) or instructor(s) you want to resend the published email to)"),c.Vb(),c.Vb(),c.Wb(5,"button",2),c.ic("click",(function(){return s.activeModal.dismiss()})),c.Rb(6,"i",3),c.Vb(),c.Vb(),c.Wb(7,"div",4),c.Oc(8,l,2,3,"div",5),c.Oc(9,d,2,0,"ng-template",null,6,c.Pc),c.Vb(),c.Wb(11,"div",7),c.Wb(12,"button",8),c.ic("click",(function(){return s.activeModal.dismiss()})),c.Qc(13,"Cancel"),c.Vb(),c.Wb(14,"button",9),c.ic("click",(function(){return s.activeModal.close(s.collateRespondentsToSendHandler())})),c.Qc(15,"Send"),c.Vb(),c.Vb()),2&e){const e=c.Cc(10);c.Cb(8),c.sc("ngIf",s.studentListInfoTableRowModels.length>0)("ngIfElse",e)}},directives:[r.u,b.a],styles:[".clickable[_ngcontent-%COMP%]{cursor:pointer}"]}),e})();var S=n("5hkX"),f=n("BBzn");class m extends f.a{constructor(e,s,n,t,o,i,c,a,r,b,l){super(e,s,n,t,o,i,c,a,r,b),this.studentService=l,this.isSendReminderLoading=!1}resendResultsLinkToRespondentsEventHandler(e){this.isSendReminderLoading=!0;const s=e.feedbackSession.courseId,n=e.feedbackSession.feedbackSessionName;Object(t.a)([this.studentService.getStudentsFromCourse({courseId:s}),this.instructorService.loadInstructors({courseId:s,intent:i.b.FULL_DETAIL})]).pipe(Object(o.a)(()=>this.isSendReminderLoading=!1)).subscribe(e=>{const t=e[0].students,i=e[1].instructors,c=this.ngbModal.open(u);c.componentInstance.courseId=s,c.componentInstance.feedbackSessionName=n,c.componentInstance.studentListInfoTableRowModels=t.map(e=>({email:e.email,name:e.name,teamName:e.teamName,sectionName:e.sectionName,hasSubmittedSession:!1,isSelected:!1})),c.componentInstance.instructorListInfoTableRowModels=i.map(e=>({email:e.email,name:e.name,hasSubmittedSession:!1,isSelected:!1})),c.result.then(e=>{this.isSendReminderLoading=!0,this.feedbackSessionsService.remindResultsLinkToRespondents(s,n,{usersToRemind:e.map(e=>e.email)}).pipe(Object(o.a)(()=>this.isSendReminderLoading=!1)).subscribe(()=>{this.statusMessageService.showSuccessToast("Session published notification emails have been resent to those students and instructors. Please allow up to 1 hour for all the notification emails to be sent out.")},e=>{this.statusMessageService.showErrorToast(e.error.message)})},()=>{})},e=>{this.statusMessageService.showErrorToast(e.error.message)})}sendRemindersToRespondentsEventHandler(e){this.isSendReminderLoading=!0;const s=e.feedbackSession.courseId,n=e.feedbackSession.feedbackSessionName;Object(t.a)([this.studentService.getStudentsFromCourse({courseId:s}),this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({courseId:s,feedbackSessionName:n}),this.instructorService.loadInstructors({courseId:s,intent:i.b.FULL_DETAIL})]).pipe(Object(o.a)(()=>this.isSendReminderLoading=!1)).subscribe(e=>{const t=e[0].students,i=new Set(e[1].giverIdentifiers),c=e[2].instructors,a=this.ngbModal.open(S.a);a.componentInstance.courseId=s,a.componentInstance.feedbackSessionName=n,a.componentInstance.studentListInfoTableRowModels=t.map(e=>({email:e.email,name:e.name,teamName:e.teamName,sectionName:e.sectionName,hasSubmittedSession:i.has(e.email),isSelected:!1})),a.componentInstance.instructorListInfoTableRowModels=c.map(e=>({email:e.email,name:e.name,hasSubmittedSession:i.has(e.email),isSelected:!1})),a.result.then(e=>{this.isSendReminderLoading=!0,this.feedbackSessionsService.remindFeedbackSessionSubmissionForRespondents(s,n,{usersToRemind:e.map(e=>e.email)}).pipe(Object(o.a)(()=>this.isSendReminderLoading=!1)).subscribe(()=>{this.statusMessageService.showSuccessToast("Reminder e-mails have been sent out to those students and instructors. Please allow up to 1 hour for all the notification emails to be sent out.")},e=>{this.statusMessageService.showErrorToast(e.error.message)})},()=>{})},e=>{this.statusMessageService.showErrorToast(e.error.message)})}}},WPPJ:function(e,s,n){"use strict";n.d(s,"a",(function(){return i}));var t=n("fXoL"),o=n("A3DQ");let i=(()=>{class e{constructor(e){this.timezoneService=e}transform(e,s){return this.timezoneService.formatToString(e,s,"D MMM h:mm A")}}return e.\u0275fac=function(s){return new(s||e)(t.Qb(o.b))},e.\u0275pipe=t.Pb({name:"formatDateBrief",type:e,pure:!0}),e})()},"iqw+":function(e,s,n){"use strict";n.d(s,"a",(function(){return i}));var t=n("ofXK"),o=n("fXoL");let i=(()=>{class e{}return e.\u0275mod=o.Ob({type:e}),e.\u0275inj=o.Nb({factory:function(s){return new(s||e)},imports:[[t.c]]}),e})()},vHwn:function(e,s,n){"use strict";n.d(s,"a",(function(){return S}));var t=n("ofXK"),o=n("3Pt+"),i=n("tyNb"),c=n("1kSV"),a=n("n6pr"),r=n("iqw+"),b=n("1GcU"),l=n("SEFB"),d=n("TOej"),u=n("fXoL");let S=(()=>{class e{}return e.\u0275mod=u.Ob({type:e}),e.\u0275inj=u.Nb({factory:function(s){return new(s||e)},imports:[[t.c,a.a,r.a,l.a,c.j,c.y,o.j,b.a,i.h,d.a]]}),e})()}}]);