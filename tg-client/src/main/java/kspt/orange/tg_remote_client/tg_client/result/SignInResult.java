package kspt.orange.tg_remote_client.tg_client.result;

import kspt.orange.rg_remote_client.commons.result.Result;

public enum SignInResult implements Result {
    OK,
    TFA_REQUIRED,
    ERROR,
    ;
}
