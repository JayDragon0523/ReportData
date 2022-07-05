package com.crowbar.reportdata.controller;

import com.crowbar.reportdata.pojo.Result;
import com.crowbar.reportdata.service.BaiduApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @program: ReportData
 * @description:
 * @author: JayDragon
 * @create: 2022-07-04 10:21
 **/
@RestController
public class DataController {

    @Autowired
    BaiduApiService baiduApiService;

    @PostMapping(value="/requestApi",produces = "application/json;charset=UTF-8")
    public String Test(@RequestBody String data){
        System.out.println(data);
        return null;
    }

    @PostMapping(value="/requestApiFile")
    public Result TestFile(MultipartFile file, @RequestParam("type") String type) throws IOException, InterruptedException {
        Result result = new Result();
        if(file != null && type != null){
            String originalFilename = file.getOriginalFilename();
            List<String> strings = baiduApiService.requestApiResult(file, type);
            result.setCode(200);
            result.setMsg("调用成功");
            result.setData(strings);
        }else{
            result.setCode(500);
            result.setMsg("调用失败，请确认参数是否正确");
        }

        return result;
    }
}
