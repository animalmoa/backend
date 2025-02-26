package com.server.animalmoa.api.crawler.service

import org.springframework.stereotype.Service
import java.awt.Robot
import java.awt.event.KeyEvent

@Service
class JavaRobotService {
    val robot = Robot()
    private val delayMillis: Long = 1000

    // 예시 함수: 입력 필드에 텍스트 붙여넣기
    fun pasteTextIntoField(text: String) {
        Thread.sleep(delayMillis)
        for (char in text) {
            // 문자에 해당하는 키 코드를 얻습니다.
            val keyCode = KeyEvent.getExtendedKeyCodeForChar(char.code)
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                throw IllegalArgumentException("키 코드가 정의되지 않은 문자: $char")
            }

            // 만약 문자가 대문자라면 SHIFT 키를 누른 상태에서 입력합니다.
            if (char.isUpperCase()) {
                robot.keyPress(KeyEvent.VK_SHIFT)
            }

            // 실제 키 누르기
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)

            if (char.isUpperCase()) {
                robot.keyRelease(KeyEvent.VK_SHIFT)
            }
            // 각 문자 사이에 약간의 딜레이를 줍니다.
            robot.delay(delayMillis.toInt())
        }
    }
}
