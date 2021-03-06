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

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/random-fact")
public class FactDataServlet extends HttpServlet {

  private static final ImmutableList<String> facts =  ImmutableList.of(
    "I do some photograph",
    "I do some videography", 
    "I have coded in 8+ different languges", 
    "I am from Mobile Alabama");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fact = facts.get((int) (Math.random() * facts.size()));

    response.setContentType("text/html;");
    response.getWriter().println(fact);
  }
}