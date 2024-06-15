package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.place.domain.entity.Category;
import com.overcomingroom.ulpet.place.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    private void initCategory() {
        // 파일 경로 읽기
        String fileLocation = "document/category_chart.txt";
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();

        List<Category> categoryList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new UrlResource(uri).getInputStream()))) {
            String line = "";

            // 파일 읽기

            while ((line = br.readLine()) != null) {

                String categoryName = line; // 카테고리 명
                Long contentTypeId = null; // 카테고리 번호

                line = br.readLine();
                String[] split = line.split(",");

                // 정규식을 이용해 카테고리 번호 추출
                Pattern pattern = Pattern.compile("(\\d+)");
                Matcher matcher = pattern.matcher(split[0]);

                if (matcher.find()) {
                    contentTypeId = Long.parseLong(matcher.group(1)); // 번호
                }

                // 소분류 명
                String subCategory = split[6];

                Category category = new Category(contentTypeId,categoryName,subCategory);
                categoryList.add(category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        categoryRepository.deleteAll();
        categoryRepository.saveAll(categoryList);
    }
}
