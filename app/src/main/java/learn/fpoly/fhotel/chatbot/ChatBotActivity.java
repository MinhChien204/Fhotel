package learn.fpoly.fhotel.chatbot;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import learn.fpoly.fhotel.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private ChatRVAdapter chatRVAdapter;
    ImageView btnback;
    // creating a variable for array list and adapter class.
    private ArrayList<ChatsModal> messageModalArrayList;

    private SharedPreferences sharedPreferences;
    private final String CHAT_PREFS = "chat_prefs";
    private final String CHAT_KEY = "chat_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        btnback = findViewById(R.id.btn_backChatBot);

        sharedPreferences = getSharedPreferences(CHAT_PREFS, MODE_PRIVATE);

        // Khôi phục cuộc trò chuyện từ SharedPreferences
        messageModalArrayList = getChatHistory();

        // creating a new array list
        messageModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatBotActivity.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);

        chatsRV.setAdapter(chatRVAdapter);

        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the message entered
                // by user is empty or not.
                if (userMsgEdt.getText().toString().isEmpty()) {
                    // if the edit text is empty display a toast message.
                    Toast.makeText(ChatBotActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling a method to send message
                // to our bot to get response.
                sendMessage(userMsgEdt.getText().toString());

                // below line we are setting text in our edit text as empty
                userMsgEdt.setText("");
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        saveChatHistory(); // Lưu cuộc trò chuyện khi Activity bị dừng lại
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageModalArrayList.clear();
        messageModalArrayList.addAll(getChatHistory());
        chatRVAdapter.notifyDataSetChanged(); // Khôi phục lại cuộc trò chuyện khi Activity được tiếp tục
    }


    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new ChatsModal(userMsg, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        saveChatHistory();

        String url = "http://api.brainshop.ai/get?bid=183592&key=i6sBDaA3t177vMyU&uid=12345&msg=" + userMsg;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.brainshop.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

        Call<MsgModal> call = retrofitApi.getMassage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, retrofit2.Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal msgModal = response.body();
                    messageModalArrayList.add(new ChatsModal(msgModal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                } else {
                    messageModalArrayList.add(new ChatsModal("No response", BOT_KEY));
                }
                chatRVAdapter.notifyDataSetChanged();
                saveChatHistory();
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable throwable) {
                Log.e("API_ERROR", "Request failed", throwable);
                messageModalArrayList.add(new ChatsModal("Sorry, no response found", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
                saveChatHistory();
            }
        });
    }
    // Lưu lịch sử cuộc trò chuyện vào SharedPreferences
    private void saveChatHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messageModalArrayList);
        editor.putString(CHAT_KEY, json);
        editor.apply();
    }

    // Khôi phục lịch sử cuộc trò chuyện từ SharedPreferences
    private ArrayList<ChatsModal> getChatHistory() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CHAT_KEY, null);
        Type type = new TypeToken<ArrayList<ChatsModal>>() {}.getType();
        if (json != null) {
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

}