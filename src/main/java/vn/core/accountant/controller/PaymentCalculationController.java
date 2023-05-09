package vn.core.accountant.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import vn.core.ReadFile;
import vn.core.Solution;
import vn.core.WriteFile;
import vn.core.accountant.dto.ConvertData;
import vn.core.accountant.dto.SolutionData;
import vn.core.accountant.dto.UploadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class PaymentCalculationController {

    @GetMapping(value = "/payment-calculation")
    public String config(Model model) {
        model.addAttribute("uploadFile", new UploadFile());
        return "payment-calculation";
    }

    @PostMapping(value = "/payment-calculation/uploadFile")
    public String upload(UploadFile uploadFile, Model model) {
        MultipartFile multipartFile = uploadFile.getMultipartFile();
        try {
            ConvertData data = ReadFile.getFromFileExcel(multipartFile);
            model.addAttribute("data", data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "payment-calculation";
    }

    @ResponseBody
    @PostMapping(value = "/payment-calculation/calculation")
    public ResponseEntity<Resource> calculation(ConvertData data, Model model) {
        try {
            List<SolutionData> solutionData = Solution.calculator(data);

            WriteFile.saveFile(solutionData, data);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output_" + new Date().getTime() + "." + FilenameUtils.getExtension(data.getFilename()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            File file = new File("/opt/input." + FilenameUtils.getExtension(data.getFilename()));

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}