package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestRejectionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Rejects an account request.
 */
public class RejectAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        String id = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);
        UUID accountRequestId;

        try {
            accountRequestId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(e.getMessage(), e);
        }

        AccountRequest accountRequest = sqlLogic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format(Const.ACCOUNT_REQUEST_NOT_FOUND, accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        AccountRequestRejectionRequest accountRequestRejectionRequest =
                getAndValidateRequestBody(AccountRequestRejectionRequest.class);
        AccountRequestStatus initialStatus = accountRequest.getStatus();

        try {
            accountRequest.setStatus(AccountRequestStatus.REJECTED);
            accountRequest = sqlLogic.updateAccountRequest(accountRequest);
            if (accountRequestRejectionRequest.checkHasReason()
                    && initialStatus != AccountRequestStatus.REJECTED) {
                EmailWrapper email = sqlEmailGenerator.generateAccountRequestRejectionEmail(accountRequest,
                        accountRequestRejectionRequest.getReasonTitle(), accountRequestRejectionRequest.getReasonBody());
                emailSender.sendEmail(email);
            }
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
