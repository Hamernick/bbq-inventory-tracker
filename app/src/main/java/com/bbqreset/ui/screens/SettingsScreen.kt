package com.bbqreset.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.bbqreset.ui.design.BBQTheme

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        SectionAccount()
        SectionBilling()
        SectionBusiness()
        SectionPassword()
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = {}) { Text("Cancel") }
            Button(onClick = {}) { Text("Update") }
        }
    }
}

@Composable
private fun SectionAccount() {
    SectionHeader("Account")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Full Name") },
            modifier = Modifier.weight(1f)
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Email Address") },
            modifier = Modifier.weight(1f)
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Phone Number") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionBilling() {
    SectionHeader("Billing")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Plan") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Billing Interval") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionBusiness() {
    SectionHeader("Business Address")
    TextField(
        value = "",
        onValueChange = {},
        label = { Text("Street Address") },
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("City") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Province") },
            modifier = Modifier.weight(1f)
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("ZIP/Postal Code") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Country") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionPassword() {
    SectionHeader("Change Password")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Confirm Password") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    BBQTheme { SettingsScreen() }
}
