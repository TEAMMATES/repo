package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestStatusUpdateResponseData;
import teammates.ui.request.AccountRequestStatusUpdateIntent;

/**
 * Approves, rejects, or resets an account request.
 */
class UpdateAccountRequestStatusAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, institute);
        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request for instructor with email: " + email
                    + " and institute: " + institute + " does not exist.");
        }

        AccountRequestStatusUpdateIntent intent =
                AccountRequestStatusUpdateIntent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        AccountRequestStatusUpdateResponseData output = new AccountRequestStatusUpdateResponseData();
        try {
            switch (intent) {
            case TO_APPROVE:
                if (accountRequest.getStatus().equals(AccountRequestStatus.REGISTERED)) {
                    throw new InvalidOperationException("Account requests with status REGISTERED cannot be approved.");
                }
                accountRequest = logic.approveAccountRequest(email, institute);

                String joinLink = accountRequest.getRegistrationUrl();
                EmailWrapper joinEmail = emailGenerator.generateNewInstructorAccountJoinEmail(
                        accountRequest.getEmail(), accountRequest.getName(), joinLink);
                emailSender.sendEmail(joinEmail);

                output.setJoinLink(joinLink);
                break;

            case TO_REJECT:
                if (accountRequest.getStatus().equals(AccountRequestStatus.REGISTERED)) {
                    throw new InvalidOperationException("Account requests with status REGISTERED cannot be rejected.");
                }
                accountRequest = logic.rejectAccountRequest(email, institute);
                break;

            case TO_RESET:
                if (accountRequest.getStatus().equals(AccountRequestStatus.APPROVED)) {
                    throw new InvalidOperationException("Account requests with status APPROVED cannot be reset."
                            + " Reject it first and then reset.");
                }
                accountRequest = logic.resetAccountRequest(email, institute);
                break;

            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException("Account request for instructor with email: " + email
                    + " and institute: " + institute + " does not exist.");
        }

        output.setAccountRequest(new AccountRequestData(accountRequest));
        return new JsonResult(output);
    }

}
