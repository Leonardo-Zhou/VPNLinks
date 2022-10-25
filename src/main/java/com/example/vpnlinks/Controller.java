package com.example.vpnlinks;

import com.google.zxing.WriterException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.io.FileUtils.delete;
import static org.apache.commons.io.FileUtils.deleteDirectory;


import static com.example.vpnlinks.Parser.*;

public class Controller {
    public ImageView QRCodeImageView;
    public Button StartButton;
    public ListView VmessListView;
    public AnchorPane AnchorPaneView;
    public ProgressBar Bar;
    public TextField VmessTextField;

    public int n = 10;

    private ArrayList<String> proxyList = new ArrayList<String>();

    private String path =System.getProperty("user.dir") + "\\Image\\";
    private String output_path = System.getProperty("user.dir") + "\\output.txt";

    private final String[] urlArray = {
            "https://www.lt71126.xyz:20000/api/evmess",
            "https://www.hd327658.xyz:20000/api/evmess",
            "https://www.09898434.xyz/api/evmess?deviceid=49c95313d64fb7c5unknown&apps=cd9186e318e291300db27867d958eae5",
            "https://www.xfjyqirx.xyz:20000/api/evmess"
    };

    private final String[] keyArray = {
            "ks9KUrbWJj46AftX",
            "ks9KUrbWJj46AftX",
            "ks9KUrbWJj46AftX",
            "awdtif20190619ti"
    };

    File file = new File(path);
    File output_file = new File(output_path);

    @FXML
protected void onButtonClick() throws IOException, InterruptedException {
        log("Button clicked");
        VmessListView.getItems().clear();

        if(file.exists()){
            deleteDirectory(file);
        }

        if (output_file.exists()){
            delete(output_file);
        }

        System.out.println("文件夹删除");
        log("File deleted");

        VmessListView.getSelectionModel().selectedItemProperty().addListener(new NoticeListItemChangeListener());

        log("Listener added");
        Task myTask = creatTaskAndRunTask();

        log("Task created");
        VmessTextField.textProperty().unbind();
        VmessTextField.textProperty().bind(myTask.messageProperty());

        VmessListView.itemsProperty().unbind();
        VmessListView.itemsProperty().bind(myTask.valueProperty());

        Bar.progressProperty().unbind();
        Bar.progressProperty().bind(myTask.progressProperty());

        myTask.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
            }

        });

        file.mkdir();
        log("file made");
		//使用新线程启动 -- 这是重中之重
        new Thread(myTask).start();
        log("Thread started");


    }

    private class NoticeListItemChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            //这里写自己的代码
            System.out.println(newValue);

            Long t = System.currentTimeMillis();
            try {
                String filePath = getQRCodeImage(newValue.toString(), (int) QRCodeImageView.getFitWidth(), (int) QRCodeImageView.getFitHeight(), path, t);
                log(filePath);
                Image image = new Image("file:" + filePath);
                log("Image got");
                QRCodeImageView.setImage(image);

            } catch (WriterException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Task creatTaskAndRunTask(){
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int all_length = n*4;
                log("Spider started");

                try {
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < urlArray.length; j++) {
                            URL url = new URL(urlArray[j]);
                            log(url.toString());
                            Document document = Jsoup.parse(url, 1000000);
                            String cipherText = document.body().text();
                            String s = decrypt(cipherText, keyArray[j]);
                            s = s.trim();
                            if (!proxyList.contains(s)){
                                proxyList.add(s);
                                System.out.println(s);

                            }
                            updateMessage(s);
                            updateProgress(i*4+j+1, all_length);

                            ObservableList<String> vmessList = FXCollections.observableArrayList();
                            for(String li:proxyList){
                                vmessList.add(li);
                            }
                            updateValue(vmessList);
                        }
                    }

                    String[] proxiesList = proxyList.toArray(new String[0]);

                    String proxiesString = StringUtils.join(proxiesList, "\n");

                    write(proxiesString, "output.txt");

                    setClipboardString(proxiesString);

                }
                catch (Exception e){
                    System.out.println(e);
                }


                return true;
            }
        };
    }
}