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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (request.getDuration() >= TimeRange.WHOLE_DAY.duration() + 1){
      return Arrays.asList();
    }

    if (request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<String> allAttendees = new ArrayList<String>(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());

    MeetingRequest optionalRequest = new MeetingRequest(allAttendees, request.getDuration());

    List<TimeRange> TimesWithOptionalAttendees = new ArrayList<TimeRange>();
    TimesWithOptionalAttendees.addAll(subQueryHandler(events,optionalRequest));

    List<TimeRange> TimesWithoutOptionalAttendees = new ArrayList<TimeRange>();
    TimesWithoutOptionalAttendees.addAll((subQueryHandler(events,request)));

    if (TimesWithOptionalAttendees.size() > TimesWithoutOptionalAttendees.size() || request.getAttendees().isEmpty()){
      return TimesWithOptionalAttendees;
    }
    return TimesWithoutOptionalAttendees;
  }

  private List<TimeRange> subQueryHandler(Collection<Event> events, MeetingRequest request) {
    List<Event> sortedRelavantEvents = new ArrayList<>();
    ArrayList<Event> eventsList = new ArrayList<>(events);

    sortedRelavantEvents = binaryIncertionSortOnTime(eventsList, request);

    return findAvalibleTimes(sortedRelavantEvents,request);
  }

  private ArrayList<Event> binaryIncertionSortOnTime(ArrayList<Event> events, MeetingRequest request){
    int i = 0;
    int left = 0;
    int right = 0;
    int middle = 0;
    Event tempEvent;
    System.out.println(events);
    while(i < events.size()){
      if(notConfilctingEvent(events.get(i), request)){
        events.remove(i);
      }
      else if (i == 0){
        i++;
        right= i - 1;
      }
      else{
        System.out.format("i= %d , left= %d , right= %d",i,left,right);
        System.out.println();
        int newlocation = binarySearchOnTime(events,events.get(i),left,right);
        System.out.format("newlocation= %d i= %d , left= %d , right= %d",newlocation,i,left,right);
        System.out.println();

        if(newlocation == -1){
          events.remove(i);
        }
        else{
          tempEvent = events.get(i);
          events.remove(i);
          events.add(newlocation,tempEvent);

          i++;
          right= i - 1;
          System.out.println(events);
        }
      }
    }

    return events;
  }

  private int binarySearchOnTime(ArrayList<Event> events,Event event,int left,int right){
    System.out.format("left= %d , right= %d", left,right);
    System.out.println();
    if (right <= left){
      if (event.getWhen().contains(events.get(left).getWhen()) || events.get(left).getWhen().contains(event.getWhen())){
        return -1;
      }
      else if (event.getWhen().start() < events.get(left).getWhen().start()){
        return left;
      }
      else {
        return left + 1;
      }
    }

    int middle = (left + right)/2;
    System.out.format("mid= %d", middle);
    System.out.println();

    if (event.getWhen().contains(events.get(middle).getWhen()) && events.get(middle).getWhen().contains(event.getWhen())){
      return -1;
    }

    if (event.getWhen().start() > events.get(middle).getWhen().start()){
      return binarySearchOnTime(events,event,(middle + 1),right);
    }
    return binarySearchOnTime(events,event,left, (middle - 1));
  }

  private List<TimeRange> findAvalibleTimes(List<Event> events, MeetingRequest request){
    if(events.size() == 0){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<TimeRange> availableTimes = new ArrayList<TimeRange>();
    Queue<TimeRange> tempTimeRanges = new LinkedList<TimeRange>();

    for(int i = 0; i < events.size(); i++){
      if(i == 0){
        tempTimeRanges.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY,events.get(i).getWhen().start(),false));
      }
      
      if(i == events.size() - 1){
        tempTimeRanges.add(TimeRange.fromStartEnd(events.get(i).getWhen().end(),TimeRange.END_OF_DAY,true));
      }
      else{
        tempTimeRanges.add(TimeRange.fromStartEnd(events.get((i)).getWhen().end(),events.get(i + 1).getWhen().start(),false));
      }
      
      while(tempTimeRanges.size() != 0){
        if(tempTimeRanges.peek().duration() >= request.getDuration()){
          availableTimes.add(tempTimeRanges.remove());
        }
        else{
          tempTimeRanges.remove();
        }
      }
      
    }

    return availableTimes;
  }

  private boolean notConfilctingEvent(Event event, MeetingRequest request){
    for(String attendee: request.getAttendees()){
      if(event.getAttendees().contains(attendee)){
        return false;
      }
    }
    return true;
  }
}

