package com.example.rabbitcalculator.calculator.domain.model

/**
 * Token 类型枚举，定义所有支持的词法单元
 */
enum class TokenType {
    // 数字
    NUMBER,

    // 基础运算符
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,

    // 科学运算符
    POWER,          // ^
    FACTORIAL,      // !
    PERCENT,        // %

    // 函数
    SIN,
    COS,
    TAN,
    LOG,            // log10
    LN,             // 自然对数
    SQRT,
    ABS,

    // 常量
    PI,
    E,

    // 括号
    LEFT_PAREN,
    RIGHT_PAREN,

    // 控制
    EOF,
    ERROR
}
