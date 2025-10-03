package com.example.helloworld

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 获取TextView和Button控件
        val helloTextView = findViewById<TextView>(R.id.helloTextView)
        val clickButton = findViewById<Button>(R.id.button)
        
        // 为按钮设置点击事件监听器
        clickButton?.setOnClickListener {
            Log.d("MainActivity", "按钮被点击了")
            // 当按钮被点击时，更改TextView的文字
            helloTextView?.text = "按钮已被点击！"
            // 显示一个提示信息
            Toast.makeText(this, "文字已更改", Toast.LENGTH_SHORT).show()
        }
    }
}