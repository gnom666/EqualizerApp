package com.example.myapplication.Model;

public final class Constants {

    public static String IP_HOME = "192.168.1.109";
    public static String IP_WORK = "10.23.51.45";
    public static String IP_EC2 = "52.47.157.126";
    public static String DNS_GLOBAL = "ec2-52-47-157-126.eu-west-3.compute.amazonaws.com";
    public static String IP = IP_HOME;
    public static String PORT = "9003";

    public static String URL_UserById = "http://" + IP + ":" + PORT + "/people/userbyid/";
    public static String URL_CheckUser = "http://" + IP + ":" + PORT + "/people/checkuser/";
    public static String URL_CheckUserAndPass = "http://" + IP + ":" + PORT + "/people/checkuserandpass/";
    public static String URL_UsersList = "http://" + IP + ":" + PORT + "/people/userslist/";
    public static String URL_Contacts = "http://" + IP + ":" + PORT + "/people/contactsbyid/";
    public static String URL_Participants = "http://" + IP + ":" + PORT + "/people/participantsbyact/";
    public static String URL_AddPerson = "http://" + IP + ":" + PORT + "/people/addperson/";
    public static String URL_SendMail = "http://" + IP + ":" + PORT + "/people/sendmail/";
    public static String URL_ModifyPerson = "http://" + IP + ":" + PORT + "/people/modifyperson/";
    public static String URL_SetFriendByEmail = "http://" + IP + ":" + PORT + "/people/setfriendbyemail/";
    public static String URL_UnsetFriends = "http://" + IP + ":" + PORT + "/people/unsetfriends/";

    public static String URL_ActivitiesByParticipant = "http://" + IP + ":" + PORT + "/activities/activitiesbyparticipant/";
    public static String URL_AddActivity = "http://" + IP + ":" + PORT + "/activities/addactivity/";
    public static String URL_ModifyActivity = "http://" + IP + ":" + PORT + "/activities/modifyactivity/";
    public static String URL_DeleteActivity = "http://" + IP + ":" + PORT + "/activities/deleteactivity/";

    public static String URL_TasksByActivity = "http://" + IP + ":" + PORT + "/tasks/tasksbyact/";
    public static String URL_AddTask = "http://" + IP + ":" + PORT + "/tasks/addtask/";
    public static String URL_ModifyTask = "http://" + IP + ":" + PORT + "/tasks/modifytask/";
    public static String URL_DeleteTask = "http://" + IP + ":" + PORT + "/tasks/deletetask/";

    public static String URL_TestPayments = "http://" + IP + ":" + PORT + "/payments/testpayments/";

    public enum ErrorCode {
        UNKNOWN,
        ACTIVITY_SERVICES,
        PAYMENTS_SERVICES,
        PERSON_SERVICES,
        TASKS_SERVICES
    }

    public static String errorCodeName (ErrorCode code) {

        String codeName;
        switch (code) {
            case ACTIVITY_SERVICES:
                codeName = "ACTIVITY_SERVICES";
                break;
            case PAYMENTS_SERVICES:
                codeName = "PAYMENTS_SERVICES";
                break;
            case PERSON_SERVICES:
                codeName = "PERSON_SERVICES";
                break;
            case TASKS_SERVICES:
                codeName = "TASKS_SERVICES";
                break;
            default:
                codeName = "UNKNOWN";
        }
        return codeName;
    }

    public static enum ErrorType {
        UNKNOWN,
        ACTIVITY_NOT_FOUND,
        PAYMENT_NOT_FOUND,
        PERSON_NOT_FOUND,
        TASK_NOT_FOUND,
        ACTIVITYOWNER_MISSMATCH,
        PAYMENTSLISTS_MISSMATCH,
        PAYMENT_USERMISSMATCH,
        PAYMENT_CLOSED,
        ZERO_PERSONS,
        INCORRECT_PASSWORD,
        BAD_DATE_FORMAT,
        EXISTENT_DATA
    }

    public static String errorTypeName (ErrorType type) {

        String typeName = "";
        switch (type) {
            case ACTIVITY_NOT_FOUND:
                typeName = "ACTIVITY_NOT_FOUND";
                break;
            case PAYMENT_NOT_FOUND:
                typeName = "PAYMENT_NOT_FOUND";
                break;
            case PERSON_NOT_FOUND:
                typeName = "PERSON_NOT_FOUND";
                break;
            case TASK_NOT_FOUND:
                typeName = "TASK_NOT_FOUND";
                break;
            case ACTIVITYOWNER_MISSMATCH:
                typeName = "ACTIVITYOWNER_MISSMATCH";
                break;
            case PAYMENTSLISTS_MISSMATCH:
                typeName = "PAYMENTSLISTS_MISSMATCH";
                break;
            case PAYMENT_USERMISSMATCH:
                typeName = "PAYMENT_USERMISSMATCH";
                break;
            case PAYMENT_CLOSED:
                typeName = "PAYMENT_CLOSED";
                break;
            case ZERO_PERSONS:
                typeName = "ZERO_PERSONS";
                break;
            case INCORRECT_PASSWORD:
                typeName = "INCORRECT_PASSWORD";
                break;
            case BAD_DATE_FORMAT:
                typeName = "BAD_DATE_FORMAT";
                break;
            case EXISTENT_DATA:
                typeName = "EXISTENT_DATA";
                break;
            default:
                typeName = "UNKNOWN";
        }
        return typeName;
    }

    public enum PaymentStatus {
        PENDING,
        REQUESTED,
        PAID,
        CONFLICT
    }

    public static String paymentStatusName (PaymentStatus status) {

        String statusName;
        switch (status) {
            case PENDING:
                statusName = "PENDING";
                break;
            case REQUESTED:
                statusName = "REQUESTED";
                break;
            case PAID:
                statusName = "PAID";
                break;
            case CONFLICT:
                statusName = "CONFLICT";
                break;
            default:
                statusName = "UNKNOWN";
        }
        return statusName;
    }

    public static PaymentStatus intToStatus (int status) {
        switch (status) {
            case 1: return PaymentStatus.REQUESTED;
            case 2: return PaymentStatus.PAID;
            case 3: return PaymentStatus.CONFLICT;
            default: return PaymentStatus.PENDING;
        }
    }

    public enum RoleType {
        ADMINISTRATOR,
        COMMON_USER,
        ADVANCED_USER,
        GUEST
    }

    public static String roleTypeName (RoleType role) {

        String roleTypeName;
        switch (role) {
            case ADMINISTRATOR:
                roleTypeName = "ADMINISTRATOR";
                break;
            case COMMON_USER:
                roleTypeName = "COMMON_USER";
                break;
            case ADVANCED_USER:
                roleTypeName = "ADVANCED_USER";
                break;
            case GUEST:
                roleTypeName = "GUEST";
                break;
            default:
                roleTypeName = "UNKNOWN";
        }
        return roleTypeName;
    }

    public static RoleType intToRoleType (int role) {
        switch (role) {
            case 1: return RoleType.ADMINISTRATOR;
            case 2: return RoleType.COMMON_USER;
            case 3: return RoleType.ADVANCED_USER;
            default: return RoleType.GUEST;
        }
    }
}
