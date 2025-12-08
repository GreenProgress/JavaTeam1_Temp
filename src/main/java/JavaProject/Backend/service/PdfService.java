package JavaProject.Backend.service;

import JavaProject.Backend.domain.AnalysisResult;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PdfService {

    public byte[] generateAnalysisResultPdf(AnalysisResult result, String customTitle) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 1. 폰트 로드 (InputStream 방식 사용 - 배포 환경 호환성)
        try {
            ClassPathResource fontResource = new ClassPathResource("static/fonts/NotoSerifKR-Light.ttf");
            byte[] fontBytes = StreamUtils.copyToByteArray(fontResource.getInputStream());
            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
            document.setFont(font);
        } catch (IOException e) {
            System.err.println("폰트 로드 실패, 기본 폰트로 진행합니다: " + e.getMessage());
        }

        // 2. 제목
        String titleText = (customTitle != null && !customTitle.isEmpty()) ? customTitle : "생활 법률 진단 결과";
        document.add(new Paragraph(titleText).setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        // 3. 진단 요약
        document.add(new Paragraph("<진단 요약>").setFontSize(14).setBold());
        document.add(new Paragraph(result.getResultSummary() != null ? result.getResultSummary() : "").setMarginBottom(10));

        // 4. 다음 절차
        document.add(new Paragraph("<행동 절차>").setFontSize(14).setBold());
        List procedureList = new List().setSymbolIndent(12).setListSymbol("•");
        if (result.getProcedures() != null) {
            result.getProcedures().forEach(p -> procedureList.add(new ListItem(p)));
        }
        document.add(procedureList);

        // 5. 필요 서류
        document.add(new Paragraph("<필요 서류>").setFontSize(14).setBold().setMarginTop(10));
        List checkList = new List().setSymbolIndent(12).setListSymbol("- ");
        if (result.getChecklist() != null) {
            result.getChecklist().forEach(c -> checkList.add(new ListItem(c)));
        }
        document.add(checkList);

        // 6. 관련 법령 근거 (추가된 부분)
        document.add(new Paragraph("<관련 법령 근거>").setFontSize(14).setBold().setMarginTop(15));

        if (result.getRelatedLaws() != null && !result.getRelatedLaws().isEmpty()) {
            for (AnalysisResult.RelatedLawInfo law : result.getRelatedLaws()) {
                
                // 법령 제목
                String lawTitle = (law.getTitle() != null) ? law.getTitle() : "관련 법령";
                document.add(new Paragraph(lawTitle)
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(8)
                        .setMarginLeft(5));

                // 조항 리스트
                if (law.getRelevantArticles() != null && !law.getRelevantArticles().isEmpty()) {
                    List articleList = new List()
                            .setSymbolIndent(15)
                            .setListSymbol("-")
                            .setMarginLeft(10)
                            .setFontSize(10);
                    
                    law.getRelevantArticles().forEach(article -> articleList.add(new ListItem(article)));
                    document.add(articleList);
                }
            }
        } else {
            document.add(new Paragraph("관련된 법령 정보가 없습니다.").setFontSize(10).setMarginLeft(5));
        }

        document.close();
        return outputStream.toByteArray();
    }
}