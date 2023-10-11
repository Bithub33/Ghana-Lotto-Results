package com.maxstudio.lotto.Utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.maxstudio.lotto.Models.Messages;

import java.util.ArrayList;

public class DiffUtilCallBack extends DiffUtil.Callback {

    ArrayList<Messages> oldMessage = new ArrayList<>();
    ArrayList<Messages> newMessage = new ArrayList<>();

    public DiffUtilCallBack(ArrayList<Messages> oldMessage, ArrayList<Messages> newMessage) {
        this.oldMessage = oldMessage;
        this.newMessage = newMessage;
    }

    @Override
    public int getOldListSize() {
        return oldMessage != null ? oldMessage.size() :0;
    }

    @Override
    public int getNewListSize() {
        return newMessage != null ? newMessage.size() :0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newMessage.get(newItemPosition).compareTo(oldMessage.get(oldItemPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Messages messages = newMessage.get(newItemPosition);
        Messages messages1 = oldMessage.get(oldItemPosition);

        Bundle bundle = new Bundle();

        if (!messages.getMessageId().equals(messages1.getMessageId()))
        {
            bundle.putString("message",messages.getMessage());
            bundle.putString("time",messages.getTime());
            bundle.putString("date",messages.getDate());
            bundle.putString("messageId",messages.getMessageId());

        }

        if (bundle.size()==0)
            return null;

        return bundle;
    }
}
