package ru.codeoff.bots.sdk.callbacks.messages;

import ru.codeoff.bots.sdk.objects.Message;
import ru.codeoff.bots.sdk.callbacks.Callback;

/**
 * Callback for message with some kind of attachments,
 * or if message has no attachments
 */
public interface OnMessageWithFwdsCallback extends Callback {

    void onMessage(Message message);
}
