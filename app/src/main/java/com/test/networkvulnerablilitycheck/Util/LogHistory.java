package com.test.networkvulnerablilitycheck.Util;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogHistory {

    String sPath; //저장위치
    static public String sDName; //로그 저장할 디렉토리 이름
    String sDPath; //sDirPath + sDirName

    public LogHistory(String sDirPath) { //생성자 실행시 폴더생성

        sPath = sDirPath;

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        sDName = "userLog_" + timeStamp.getTime()/1000;
        sDPath = sPath + "/" + sDName;

        File file = new File(sDPath);
        file.mkdirs();
        saveLog("192.168.0.1(IPTIME11)", "sdfksdfjsdklfjskldjfsklfjksjfkls\r\n");
    }

    public void saveLog(String sFileName, String sContents) {

        try {
            File file = new File(sDPath +"/"+ sFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file,true));
            bufferedWriter.newLine();
            bufferedWriter.write(sContents);
            bufferedWriter.close();

        }catch (Exception e){ //에러 떳을때 로그삭제
            deleteDir();
        }

        return;

    }

    public String[][] loadLog(String sLoadDirName) {

        File fPath = new File(sPath + "/" + sLoadDirName); //파일저장되어 있는 절대주소 가져오기
        File[] fileList = fPath.listFiles(); //리스트화
        ArrayList<String> sArrayList = new ArrayList<>();
        for(File tempFile : fileList) {
            if(tempFile.isFile()) {
                sArrayList.add(tempFile.getName());
            }
        }


        String[] sFileName = new String[ sArrayList.size() ];
        for (int i=0; i < sFileName.length; i++)
        {
            sFileName[i] =  sArrayList.get(i);
        }

        String[][] result = new String[sArrayList.size() ][];

        for (int i=0; i < sFileName.length; i++) { //모두 파일 다 불러옴
            try {
                FileInputStream fileInputStream = new FileInputStream(sPath +  "/" + sLoadDirName + "/" + sFileName[i]);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String temp = "";
                sArrayList = new ArrayList<>();
                while ((temp = bufferReader.readLine()) != null) {
                    sArrayList.add(temp);
                }

                result[i] = new String[ sArrayList.size() + 1 ];
                result[i][0] = sFileName[i];

                for (int j=0; j < result[i].length; j++)
                {
                    result[i][j+1] =  sArrayList.get(j);
                }

            } catch (Exception e) {
            }
        }

        return result;

    }

    public String[] loadDir() { //불러올 정보의 파일이름과 몇번째까지 불러올 것인지

        String sDirList = ""; //정규식 가공전 임시 결과값
        File fDir = new File(sPath); //파일 목록 가져오기
        File[] listDir = fDir.listFiles(); //결과 리스트화
        for(File tempFile : listDir) {
            if(tempFile.isDirectory()) {
                sDirList += tempFile.getName();
            }
        }

        ArrayList<Integer> mArrayList = new ArrayList<Integer>();
        //userLog를 찾아낸 다음에 숫자를 추출함. 에러를 줄이기 위함
        Pattern pattern_1 = Pattern.compile("userLog_\\d{10,10}");
        Matcher match_1 = pattern_1.matcher(sDirList);

        while (match_1.find()) {
            String match_1_result =  match_1.group();

            Pattern pattern_2 = Pattern.compile("\\d{10,10}");
            Matcher match_2 = pattern_2.matcher(match_1_result);

            while (match_2.find()) { //정규식 해당되는 부분을 만났을시 true 반환
                mArrayList.add(Integer.parseInt(match_2.group())); //정규식에 해당되는 내용 숫자로 반환
            }
        }

        Collections.reverse(mArrayList); //내림차순 정렬

        String[] saDirList= new String[ mArrayList.size()*2 ];
        for (int i=0, j=0; i < saDirList.length; ++i, ++j) {
            Log.i("TTTT", Integer.toString(i));
            saDirList[i] =  "userLog_" + Integer.toString(mArrayList.get(j).intValue());

            try {
                FileInputStream fileInputStream = new FileInputStream(sPath + "/" + saDirList[i] + "/" + "Info");
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fileInputStream));
                ++i;
                bufferReader.readLine();
                saDirList[i] = bufferReader.readLine();
            } catch (Exception e) { }
        }

        return saDirList; //끝남.
    }


    public void deleteDir(){ //폴더 및 파일 삭제
        File file = new File(sDPath); //폴더내 파일을 배열로 가져온다.
        File[] fileList = file.listFiles();

        for (int i = 0; i < fileList.length; i++)
        {
            File file2 = fileList[i];
            if(file2.isFile()){
                file2.delete();
            }
        }

        file.delete();
    }


}
