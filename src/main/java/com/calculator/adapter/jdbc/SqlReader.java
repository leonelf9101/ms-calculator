package com.calculator.adapter.jdbc;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import com.calculator.adapter.jdbc.exception.SqlReaderException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@UtilityClass
@Slf4j
public class SqlReader {

    /**
     * Dado una URI la convierte en String
     *
     * @param sqlPath El path se encuentra el .sql
     * @return Un string que tiene el contenido del archivo sql
     */
    public String readSql(final String sqlPath) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(sqlPath);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8.name())) {
            textBuilder.append(FileCopyUtils.copyToString(reader));
        } catch (IOException ex) {
            log.error("Error al leer el archivo sql",ex);
            throw new SqlReaderException(ex);
        }

        return textBuilder.toString().replaceAll("\n", " ");
    }
}