package joe.frame.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import joe.frame.utils.LogUtils;

/**
 * Description Socket连接异步任务类<br>
 * 每次使用需要重新实例化一个对象，不能重复使用。
 *
 * @author chenqiao
 */
public class SocketAsyncTask {

    private Socket socket = null;
    private BufferedReader reader;
    private BufferedWriter writer;
    private InputStream input;
    private OutputStream output;

    //IP地址
    private String address;
    //端口号
    private int port;

    private boolean isConnected = false;

    private ReconnectThread reconnectThread;

    private ConnectListener listener;

    private boolean isNeedStop = false;

    private SocketAsyncTask() {

    }

    public SocketAsyncTask(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect(ConnectListener listener) {
        this.listener = listener;
        asyncTask.execute();
    }

    public boolean isConnected() {
        return isConnected;
    }

    private AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (reconnectThread != null) {
                    reconnectThread.interrupt();
                }
                //连接socket
                LogUtils.d("Socket start socket connect:" + address + ":" + port);
                socket = new Socket(address, port);
                socket.setKeepAlive(true);
                input = socket.getInputStream();
                output = socket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                reconnectThread = null;
                reconnectThread = new ReconnectThread(socket);
                reconnectThread.start();
                if (listener != null) {
                    //链接成功
                    listener.onConnected();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                if (reconnectThread != null) {
                    reconnectThread.interrupt();
                    reconnectThread = null;
                }
                if (listener != null) {
                    //链接失败
                    listener.onFailedConnect();
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isconnected) {
            isConnected = isconnected;
        }
    };

    public class ReconnectThread extends Thread {

        private Socket savesocket;

        public ReconnectThread(Socket socketparam) {
            this.savesocket = socketparam;
        }

        //断线监听
        @Override
        public void run() {
            LogUtils.d("Socket is " + isInterrupted());
            while (!isInterrupted() && !isNeedStop) {
                if (this.savesocket != null) {
                    try {
                        LogUtils.d("Socket send heart package");
                        this.savesocket.sendUrgentData(0xff);
                    } catch (IOException e) {
                        //说明断线了
                        e.printStackTrace();
                        LogUtils.d("Socket disconnected");
                        isConnected = false;
                        if (listener != null) {
                            listener.onDisconnected();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //关闭Socket
    public void stopSocket() throws IOException {
        isNeedStop = true;
        if (reconnectThread != null) {
            reconnectThread.interrupt();
        }
        if (socket != null) {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            socket.close();
        }
    }

    //发送消息到服务器
    public synchronized boolean sendString(String s) throws IOException {
        if (writer != null) {
            writer.write(s);
            writer.flush();
            return true;
        }
        return false;
    }

    //获取一行消息（以/r,/n,/r/n结尾为一行,若服务端发送的消息不以上述标识结尾，则接收不到）
    public String readString() throws IOException {
        if (reader != null) {
            return reader.readLine();
        }
        return null;
    }

    public synchronized boolean sendBytes(byte[] bytes) throws IOException {
        if (output != null) {
            output.write(bytes);
            output.flush();
            return true;
        }
        return false;
    }

    public byte[] readBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (input != null) {
            byte[] buffer = new byte[1024];
            int len = 0;

            len = input.read(buffer);
            if (len != -1) {
                stream.write(buffer, 0, len);
            }
            LogUtils.d("Socket write finished:" + len + input);
            stream.flush();
        }
        return stream.toByteArray();
    }

    public int readBytes(byte[] bytes) throws IOException {
        return input.read(bytes);
    }

    //关闭输出流
    public void stopOutput() throws IOException {
        if (output != null) {
            output.close();
        }
        if (writer != null) {
            writer.close();
        }
    }

    //关闭输入流
    public void stopInput() throws IOException {
        if (input != null) {
            input.close();
        }
        if (reader != null) {
            reader.close();
        }
    }

    public interface ConnectListener {
        void onConnected(); //第一次连接成功后的回调

        void onDisconnected();  //连接后断开的回调

        void onFailedConnect(); //第一次连接失败后的回调
    }
}