package com.gilbersoncampos.switchblade.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gilbersoncampos.switchblade.R

@Composable
fun ItemMenuComponent(@DrawableRes icon:Int, title:String,onClick:()->Unit={}) {
    Card(Modifier.clickable { onClick() }) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = "QrCode"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title)
        }
    }
}

@Composable
@Preview
fun ItemMenuComponentPreview() {
    Column(Modifier.fillMaxSize()) {

    ItemMenuComponent(icon=R.drawable.baseline_qr_code_scanner_24,"Opção")
    }
}