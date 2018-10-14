//package com.example.yusuf.beaconcheck;
//
//import java.util.ArrayList;
//
//public class InstructorClass {
//
//        public static String encodeLecture(String date, String[] emails){
//            String ret = date;
//            for(String email: emails){
//                ret += "--" + email;
//            }
//            return ret;
//        }
//
//        public static String encodeString(String id, String[] lectures){
//            return
//        }
//
//        public static String decodeString(){
//
//        }
//
//        public static String getIdFromString(String encodedString){
//            return encodedString.substring(0, 5);
//        }
//        public static String getDateFromLecture(String lecture){
//            return lecture.substring(0, 8);
//        }
//
//        public static String[] getEmailsFromLecture(String lecture){
//            ArrayList<String> emails = new ArrayList<String>();
//            lecture = lecture.substring(5, lecture.length());
//            String buffer = "";
//            for(int i = 0; i < lecture.length(); i++){
//                if(lecture.substring(i, i+2) == "--"){
//                    emails.add(buffer);
//                    buffer = "";
//                    i++;
//                }
//                else{
//                    buffer += lecture.charAt(i);
//                }
//            }
//            String[] ret = new String[emails.size()];
//            emails.toArray(ret);
//            return ret;
//        }
//
//
//    }
//}
