package com.simonalong.mikilin.util;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author zhouzhenyong
 * @since 2018/1/4 上午10:52
 */
@Slf4j
@UtilityClass
public class FileUtil {

    /**
     * 读取资源文件中的内容
     *
     * @param cls              类所在的位置
     * @param resourceFileName 资源文件中的位置比如：/script/base.groovy，其中前面一定要有"/"
     * @return 文件的字符数据
     */
    public String readFromResource(Class cls, String resourceFileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = cls.getResourceAsStream(resourceFileName);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } finally {
            inputStream.close();
            assert bufferedReader != null;
            bufferedReader.close();
        }
        return stringBuilder.toString();
    }
}
