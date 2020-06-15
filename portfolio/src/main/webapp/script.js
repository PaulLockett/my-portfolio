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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Animal');
  data.addColumn('number', 'Count');
  data.addRows([
    ['Lions', 10],
    ['Tigers', 5],
    ['Bears', 15]
  ]);

  const options = {
    'title': 'Zoo Animals',
    'width':500,
    'height':400,
    'backgroundColor': 'black'
  };

  const chart = new google.visualization.PieChart(
    document.getElementsByClassName('chart-container')[0]
  );

  chart.draw(data, options);
}

/**
 * Adds a random greeting to the page.
 */
async function addRandomFact() {

  const response = await fetch('/random-fact');
  const fact = await response.text();

  // Add it to the page.
  document.getElementsByClassName('fact-container')[0].innerText = fact;
}

async function showComments() {

  clearListElements();

  const maxComments = document.getElementsByName('maxComments')[0].value;
  const languageCode = document.getElementsByClassName('language')[0].value;

  const response = await fetch(`/comments?maxComments=${maxComments}&languageCode=${languageCode}`);
  const comments = await response.json();
  
  // Add it to the page.
  for(idx in comments){
    addListElemenToDom(comments[idx]);
  }
}

async function deleteCommentData() {

  const request = new Request('/delete-data', {method: 'POST'});

  const response = await fetch(request);

  showComments();
}

function clearListElements(){
  const commentListElement = document.getElementsByClassName('comment-list')[0];

  commentListElement.innerHTML = '';
}

function addListElemenToDom(comment) {
  const commentListElement = document.getElementsByClassName('comment-list')[0];

  commentListElement.appendChild(createListElement(comment)); 
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
