package com.suypower.pms.view.plugin.chat;

import android.view.View;

/**
 * Created by Stereo on 16/4/8.
 */
public interface IChat {

    void OnMoreItem(InputView.ChatKeyType chatKeyType);
    void OnEmoji(String emoji,String filename);
    void Ondelete();
    void OnClickChatItem(ChatMessage chatMessage);
    void OnLongClickItem(ChatMessage chatMessage, View view);
    void OnClickSender(String senderid);
}
