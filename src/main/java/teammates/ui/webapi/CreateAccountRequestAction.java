package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(instructorEmail, instructorInstitution)
                .withName(instructorName)
                .build();

        try {
            accountRequestAttributes = logic.createOrUpdateAccountRequest(accountRequestAttributes);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.CREATE_ACCOUNT_PAGE)
                .withRegistrationKey(accountRequestAttributes.getRegistrationKey())
                .toAbsoluteString();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
