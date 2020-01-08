package kspt.orange.tg_remote_client.drive.result;

import kspt.orange.rg_remote_client.commons.result.Result;

public enum AttachTokenResult implements Result {
    OK,
    WRONG_ID_TOKEN,
    WRONG_SERVER_AUTH_CODE,
    NOT_ENOUGH_RIGHTS,
    ERROR,
    ;
}
