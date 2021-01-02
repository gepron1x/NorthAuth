package ru.codeoff.bots.sdk.callbacks.callbackapi.wall;

import ru.codeoff.bots.sdk.callbacks.Callback;
import org.json.JSONObject;

/**
 * See more: <a href="https://vk.com/dev/callback_api">link</a>.
 */
public interface OnWallReplyRestoreCallback extends Callback {

    void callback(JSONObject object);
}