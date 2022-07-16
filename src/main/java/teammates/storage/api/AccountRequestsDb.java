package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.storage.entity.AccountRequest;
import teammates.storage.search.AccountRequestSearchManager;
import teammates.storage.search.SearchManagerFactory;

/**
 * Handles CRUD operations for account requests.
 *
 * @see AccountRequest
 * @see AccountRequestAttributes
 */
public final class AccountRequestsDb extends EntitiesDb<AccountRequest, AccountRequestAttributes> {

    private static final AccountRequestsDb instance = new AccountRequestsDb();

    private AccountRequestsDb() {
        // prevent initialization
    }

    public static AccountRequestsDb inst() {
        return instance;
    }

    private AccountRequestSearchManager getSearchManager() {
        return SearchManagerFactory.getAccountRequestSearchManager();
    }

    /**
     * Creates or updates search document for the given account request.
     */
    public void putDocument(AccountRequestAttributes accountRequest) throws SearchServiceException {
        getSearchManager().putDocument(accountRequest);
    }

    /**
     * Searches all account requests in the system.
     *
     * <p>This is used by admin to search account requests in the whole system.
     */
    public List<AccountRequestAttributes> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchAccountRequests(queryString);
    }

    /**
     * Gets an account request by email and institute.
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        return makeAttributesOrNull(getAccountRequestEntity(email, institute));
    }

    /**
     * Gets all account requests with status {@code AccountRequestStatus.SUBMITTED}.
     */
    public List<AccountRequestAttributes> getAccountRequestsWithStatusSubmitted() {
        return makeAttributes(getAccountRequestEntitiesWithStatusSubmitted());
    }

    /**
     * Updates an account request.
     *
     * <p>If the email or institute of the account request is changed, the account request is re-created.
     * During re-creation, if an account request with the new email and new institute already exists, checks the status of
     * the existing account request. If and only if its status is REJECTED and {@code isForceUpdate} equals {@code true},
     * deletes that account request and update the current account request normally.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the new account request is not valid
     * @throws EntityDoesNotExistException if the account request to update cannot be found
     * @throws EntityAlreadyExistsException if the account request cannot be updated by re-creation because
     *                                      the above-mentioned condition is not met
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions,
                                                         boolean isForceUpdate)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert updateOptions != null;

        AccountRequest accountRequest = getAccountRequestEntity(updateOptions.getEmail(), updateOptions.getInstitute());
        if (accountRequest == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        AccountRequestAttributes newAccountRequestAttributes = makeAttributes(accountRequest);
        newAccountRequestAttributes.update(updateOptions);

        newAccountRequestAttributes.sanitizeForSaving();
        if (!newAccountRequestAttributes.isValid()) {
            throw new InvalidParametersException(newAccountRequestAttributes.getInvalidityInfo());
        }

        boolean isEmailOrInstituteChanged = !accountRequest.getEmail().equals(newAccountRequestAttributes.getEmail())
                || !accountRequest.getInstitute().equals(newAccountRequestAttributes.getInstitute());

        if (isEmailOrInstituteChanged) {
            // check existing account request
            AccountRequest existingAccountRequest = getAccountRequestEntity(newAccountRequestAttributes.getEmail(),
                    newAccountRequestAttributes.getInstitute());
            if (existingAccountRequest != null && existingAccountRequest.getStatus().equals(AccountRequestStatus.REJECTED)
                    && isForceUpdate) {
                // force update by deleting the existing account request
                deleteAccountRequest(newAccountRequestAttributes.getEmail(), newAccountRequestAttributes.getInstitute());
            }

            // create the updated account request
            newAccountRequestAttributes = createEntity(newAccountRequestAttributes);
            // delete the old account request
            deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        } else {
            // update only if change
            boolean hasSameAttributes = hasSameValue(accountRequest.getName(), newAccountRequestAttributes.getName())
                    && hasSameValue(accountRequest.getHomePageUrl(), newAccountRequestAttributes.getHomePageUrl())
                    && hasSameValue(accountRequest.getComments(), newAccountRequestAttributes.getComments())
                    && hasSameValue(accountRequest.getStatus(), newAccountRequestAttributes.getStatus())
                    && hasSameValue(accountRequest.getRegisteredAt(), newAccountRequestAttributes.getRegisteredAt());
            if (hasSameAttributes) {
                log.info(String.format(
                        OPTIMIZED_SAVING_POLICY_APPLIED, AccountRequest.class.getSimpleName(), updateOptions));
            } else {
                saveEntity(newAccountRequestAttributes.toEntity());
            }
        }

        return newAccountRequestAttributes;
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * @return the account request or null if no match found
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        List<AccountRequest> accountRequestList = load().filter("registrationKey =", registrationKey).list();

        if (accountRequestList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + accountRequestList.stream().map(i -> i.getId()).collect(Collectors.joining(", ")));
        }

        if (accountRequestList.isEmpty()) {
            return null;
        }

        return makeAttributes(accountRequestList.get(0));
    }

    private AccountRequest getAccountRequestEntity(String id) {
        return load().id(id).now();
    }

    private AccountRequest getAccountRequestEntity(String email, String institute) {
        return getAccountRequestEntity(AccountRequest.generateId(email, institute));
    }

    private List<AccountRequest> getAccountRequestEntitiesWithStatusSubmitted() {
        return load()
                .filter("status", AccountRequestStatus.SUBMITTED)
                .list();
    }

    /**
     * Deletes an accountRequest.
     */
    public void deleteAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        deleteDocumentByAccountRequestId(AccountRequest.generateId(email, institute));
        deleteEntity(Key.create(AccountRequest.class, AccountRequest.generateId(email, institute)));
    }

    /**
     * Removes search document for the given account request by using {@code accountRequestUniqueId}.
     */
    public void deleteDocumentByAccountRequestId(String accountRequestUniqueId) {
        getSearchManager().deleteDocuments(Collections.singletonList(accountRequestUniqueId));
    }

    @Override
    LoadType<AccountRequest> load() {
        return ofy().load().type(AccountRequest.class);
    }

    @Override
    boolean hasExistingEntities(AccountRequestAttributes entityToCreate) {
        Key<AccountRequest> keyToFind = Key.create(AccountRequest.class,
                AccountRequest.generateId(entityToCreate.getEmail(), entityToCreate.getInstitute()));
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    AccountRequestAttributes makeAttributes(AccountRequest entity) {
        assert entity != null;

        return AccountRequestAttributes.valueOf(entity);
    }

    /**
     * Gets the number of account requests created within a specified time range.
     */
    public int getNumAccountRequestsByTimeRange(Instant startTime, Instant endTime) {
        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .count();
    }

}
