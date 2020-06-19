// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/comments")
public class CommentsDataServlet extends HttpServlet {

  private ArrayList<String> comments = new ArrayList<String>();
  static final Query query = new Query("comment");
  static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  static final Translate translate = TranslateOptions.getDefaultInstance().getService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    System.out.println(request.getParameter("languageCode"));
    int maxComments = Integer.parseInt(request.getParameter("maxComments"));
    String languageCode = request.getParameter("languageCode");

    populateCommentsList(maxComments);

    translateComments(languageCode);

    String json = convertToJsonUsingGson();

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException {

    String text = request.getParameter("text-input");

    if(text != null){

      Entity commentEntity = new Entity("comment");
      commentEntity.setProperty("text", text);

      datastore.put(commentEntity);
    }

    response.sendRedirect("/index.html");
  }

  private String convertToJsonUsingGson() {
    Gson gson = new Gson();

    String json = gson.toJson(comments,ArrayList.class);
    return json;
  }

  private void populateCommentsList(int maxComments){
    comments.clear();
    
    PreparedQuery results = datastore.prepare(query);
  
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxComments))) {

      if (entity.getProperty("text").toString() instanceof String){

        String comment = entity.getProperty("text").toString();

        comments.add(comment);
      }

    }
  }

  private void translateComments(String languageCode){

    for(int i = 0; i < comments.size();i++){
    Translation translation =
        translate.translate(comments.get(i),
        Translate.TranslateOption.targetLanguage(languageCode));
    String translatedText = translation.getTranslatedText();
    comments.set(i,translatedText);
    }

  }
}