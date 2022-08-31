package martin.zhang.mail

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

/**
 * Intent.ACTION_SENDTO         无附件地发送邮件
 * Intent.ACTION_SEND           有附件地发送邮件
 * Intent.ACTION_SEND_MULTIPLE  带有多附件的发
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
        private const val EMAIL_PACKAGE_NAME = "com.android.email"
    }

    private val emailReceiver = arrayOf("124563@qq.com", "654321@163.com")
    private val emailTitle = "Your e-mail title"
    private val emailContent = "Your e-mail content"
    private val fileUri by lazy { getFileUri() }

    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var tv3: TextView
    private lateinit var tv4: TextView
    private lateinit var tv5: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        initView()
    }

    private fun checkPermissions(permissions: List<String>) {
        val revokedPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }
        if (revokedPermissions.isEmpty()) {
            Toast.makeText(this, "All permissions got.", Toast.LENGTH_LONG).show()
            return
        }
        ActivityCompat.requestPermissions(
            this,
            revokedPermissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST_CODE) return
        val rejectedPermission: MutableList<String> = mutableListOf()
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                rejectedPermission.add(permission)
            }
        }
        if (rejectedPermission.isEmpty()) {
            Toast.makeText(this, "All permissions got.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "You didn't permit $rejectedPermission!", Toast.LENGTH_LONG).show()
        }
    }

    private fun initView() {
        tv1 = findViewById(R.id.tv1)
        tv2 = findViewById(R.id.tv2)
        tv3 = findViewById(R.id.tv3)
        tv4 = findViewById(R.id.tv4)
        tv5 = findViewById(R.id.tv5)

        tv1.setOnClickListener {
            openEmail1()
        }
        tv2.setOnClickListener {
            openEmail2()
        }
        tv3.setOnClickListener {
            openEmail3()
        }
        tv4.setOnClickListener {
            openEmail4()
        }
        tv5.setOnClickListener {
            openEmail5()
        }
    }

    private fun openEmail1() {
        val intent = Intent(Intent.ACTION_SENDTO)
        val uri = Uri.parse("mailto://455245521@qq.com")
        intent.data = uri
        intent.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
        intent.putExtra(Intent.EXTRA_TEXT, emailContent)
        //check if the target app is available or not
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun openEmail2() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            this.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun openEmail3() {
        val intent = Intent(Intent.ACTION_SEND)
        // 邮件发送类型：无附件，纯文本
        intent.type = "plain/text"
        intent.apply {
            //设置邮件地址
            putExtra(Intent.EXTRA_EMAIL, emailReceiver)
            //设置邮件标题
            intent.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
            //设置发送的内容
            intent.putExtra(Intent.EXTRA_TEXT, emailContent)
        }
        // 展示手机中能处理这个intent的所有应用
        startActivity(Intent.createChooser(intent, "请选择邮件发送软件"))
    }

    private fun openEmail4() {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, emailReceiver)
            intent.setPackage(EMAIL_PACKAGE_NAME)
            startActivity(Intent.createChooser(intent, "test opening your system email"))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun openEmail5() {
        val intent = Intent(Intent.ACTION_SEND)
        // 邮件发送类型：有附件
        intent.type = "application/octet-stream"
        intent.apply {
            // 设置邮件地址
            putExtra(Intent.EXTRA_EMAIL, emailReceiver)
            // 设置邮件标题
            intent.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
            // 设置发送的内容
            intent.putExtra(Intent.EXTRA_TEXT, emailContent)
            // 文件附件
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        // 展示手机中能处理这个intent的所有应用
        startActivity(Intent.createChooser(intent, "请选择邮件发送软件"))
    }

    @JvmName("getFileUri1")
    private fun getFileUri(): Uri {
        val fileName = "wifi_config.txt"
        val filePath = "${Environment.getExternalStorageDirectory().absolutePath}/$fileName"
        val file = File(filePath)
        if (file.createNewFile()) {
            Toast.makeText(this, "create file $fileName", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "file $fileName exists", Toast.LENGTH_LONG).show()
        }
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }
}
