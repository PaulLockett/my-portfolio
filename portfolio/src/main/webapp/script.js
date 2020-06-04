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

/**
 * Adds a random greeting to the page.
 */
async function addRandomFact() {

  const response = await fetch('/random-fact');
  const fact = await response.text();

  // Add it to the page.
  document.getElementsByClassName('fact-container')[0].innerText = fact;
}


document.addEventListener('DOMContentLoaded', function() {
   showComments();
}, false);

async function showComments() {

  const response = await fetch('/comments');
  const comments = await response.json();
  
  // Add it to the page.
  for(idx in comments){
    addListElemenToDom(comments[idx]);
  }
}

function addListElemenToDom(comment) {
  const commentListElement = document.getElementsByClassName('comment-list')[0];

  commentListElement.appendChild(createListElement(comment)); 
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
