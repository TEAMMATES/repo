package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
class GetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        AccountAttributes accountInfo = logic.getAccount(googleId);

        if (accountInfo == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        if (accountInfo.isMigrated()) {
            Account account = sqlLogic.getAccountByGoogleId(googleId);

            if (account == null) {
                throw new EntityNotFoundException("Account does not exist.");
            }

            AccountData output = new AccountData(account);
            return new JsonResult(output);
        } else {
            AccountData output = new AccountData(accountInfo);
            return new JsonResult(output);
        }
    }

}
