package com.ezcoins.project.coin.udun;

import java.util.Map;

public interface Client<T> {

    ResponseMessage<T> post(String url, Map<String, String> list);

    ResponseMessage<T> get(String url);

}
