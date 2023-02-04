export interface SupportRequest extends SupportRequestRequest {
    trackingId: string, 
    status: SupportReqStatus
}

export interface SupportRequestRequest {
    email: string, 
    name: string, 
    enquiry_type: SupportReqEnquiryType, 
    title: string, 
    initial_msg: string,
}

/**
 * The ordering in the supportRequestList is based on the order of this list
 */
export enum SupportReqStatus {
    NEW, 
    AWAITING_ADMIN, 
    AWAITING_USER,
    RESOLVED
}

/**
 * The ordering in the supportRequestList is based on the order of this list
 */
export enum SupportReqEnquiryType { 
    NEW_ACCOUNT, 
    GENERAL_HELP
}