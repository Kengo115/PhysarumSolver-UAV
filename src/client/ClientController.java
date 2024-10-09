package client;

import java.util.ArrayList;

public class ClientController {
    static private ArrayList<Client> clientList;
    //クライアントをコントローラに追加する
    public void addClient(Client client){
        clientList.add(client);
    }

    //要求完了クライアントをリストから削除する
    public void removeClient(Client client){
        clientList.remove(client);
    }

    //クライアントリストを返す
    public ArrayList<Client> getClientList(){
        return clientList;
    }

    //クライアントリストから任意のクライアントを返す
    public Client getClient(int i){
        return clientList.get(i);
    }
}
