package com.example.cash_ratio_analyzer_test.service;

import com.example.cash_ratio_analyzer_test.entity.XbrlData;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class XbrlParserService {

    private final AccountService accountService;

    private DocumentBuilder documentBuilder;

    public XbrlParserService(AccountService accountService) throws ParserConfigurationException {
        this.accountService = accountService;
        var factory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = factory.newDocumentBuilder();
    }

    // TODO SAXパーサーで逐次解析の方がメモリ効率が良い
    public List<XbrlData> parseXbrl(byte[] xbrlContent) {
        var result = new ArrayList<XbrlData>();

        var document = parseDocumentToDom(xbrlContent);
        var elements = document.getDocumentElement();
        // TODO タグについては要検討
        var nodeList = elements.getElementsByTagName("ix:nonFraction");

        if (nodeList.getLength() == 0) {
            return result;
        }

        // TODO ここで取得する情報（科目）を選別する > accountsテーブルに取得する科目をマスタデータとして登録しておく想定
        // 企業データは登録済の想定, 企業に紐づく文書データは別APIで事前登録済の想定
        // ここで保存する科目は、登録済の文書データに紐づく必要がある
        for (int i = 0; i < nodeList.getLength(); i++) {
            var element = (Element) nodeList.item(i);
            var xbrlData = extractXbrlData(element);

            if (xbrlData == null) {
                continue;
            }
            result.add(xbrlData);
        }

        return result;
    }

    private XbrlData extractXbrlData(Element element) {
        var targetAccounts = accountService.getAccountNames();
        var name = element.getAttribute("name");
        if (name.isEmpty() || !targetAccounts.contains(name)) {
            return null;
        }
        // 今期か前期は判断できる
        var contextRef = element.getAttribute("contextRef");
        var unitRef = element.getAttribute("unitRef");
        var value = element.getTextContent();

        // TODO FinancialDataクラスへ差し替え
        return new XbrlData(name, contextRef, unitRef, value);
    }

    /**
     * XBRLコンテンツをDOMドキュメントに解析します。
     *
     * @param xbrlContent 解析するXBRLコンテンツのバイト配列
     * @return 解析されたDOMドキュメント
     * @throws RuntimeException XBRLコンテンツの解析に失敗した場合
     */
    private Document parseDocumentToDom(byte[] xbrlContent) {
        try {
            return documentBuilder.parse(new ByteArrayInputStream(xbrlContent));
        } catch (SAXException | IOException e) {
            throw new RuntimeException("Failed to parse XBRL content: " + e.getMessage());
        }
    }
}
