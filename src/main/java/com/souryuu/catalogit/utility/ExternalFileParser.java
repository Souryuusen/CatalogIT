package com.souryuu.catalogit.utility;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Data @NoArgsConstructor
public class ExternalFileParser {

    public static void main(String[] args) {
    }

    public List<String> parseMovieLine(String inputString) {
        return Arrays.stream(inputString.split("\t")).map(String::trim).filter(s -> !s.equalsIgnoreCase(""))
                .toList();
    }

    public static File selectFile(boolean saveFlag) {
        return null;
    }

}
