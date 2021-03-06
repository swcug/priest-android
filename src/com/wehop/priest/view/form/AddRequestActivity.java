package com.wehop.priest.view.form;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wehop.priest.R;
import com.wehop.priest.business.Me;
import com.wehop.priest.business.structure.notify.AddRequestNotify;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;


/**
 * 好友添加请求
 */
@ResourceView(id = R.layout.activity_addrequest)
public class AddRequestActivity extends OnlyUserActivity {
	@ResourceView(id = R.id.addrequest_button_close)
	public ImageButton btnClose;
	@ResourceView(id = R.id.addrequest_label_name)
	public TextView labName;
	@ResourceView(id = R.id.addrequest_label_description)
	public TextView labDescription;
	@ResourceView(id = R.id.addrequest_button_refuse)
	public Button btnRefuse;
	@ResourceView(id = R.id.addrequest_button_accept)
	public Button btnAccept;

    private AddRequestNotify model = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        model = (AddRequestNotify) this.getIntent().getSerializableExtra("message");
        if(null == model){
            finish();
            return;
        }
        if(model.hasRead) {
        	btnRefuse.setText("已处理");
        	btnRefuse.setEnabled(false);
        	btnAccept.setVisibility(View.GONE);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRequestActivity.this.finish();
            }
        });
        labName.setText(model.applicantNickname + "(" + model.applicantPhone + ")");
        labDescription.setText("请求添加您为：" + model.relation);
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Networking.doCommand("ResponseFriend", new JSONResponse(AddRequestActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content) {
							return;
						}
						AddRequestActivity.this.finish();
					}
                }, Me.instance.token, false, model.requestId);
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Networking.doCommand("ResponseFriend", new JSONResponse(AddRequestActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null != content && content.getInteger("code") > 0) {
							Me.instance.refreshDoctor(AddRequestActivity.this, new IEventable<Boolean>() {
								@Override
								public void on(Boolean data) {
									AddRequestActivity.this.finish();
								}
							});
//							Me.instance.refreshPatient(AddRequestActivity.this, new IEventable<Boolean>() {
//								@Override
//								public void on(Boolean data) {
//									AddRequestActivity.this.finish();
//								}
//							});
						}
					}
                }, Me.instance.token, true, model.requestId);
            }
        });
    }
}
