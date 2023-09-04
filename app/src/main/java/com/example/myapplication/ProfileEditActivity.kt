package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.myapplication.Classes.MyApplication
import com.example.myapplication.databinding.ActivityProfileEditBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileEditActivity : AppCompatActivity() {
    //viewbinding
    private lateinit var binding: ActivityProfileEditBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //image url
    private var imageUri: Uri?=null

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        loadUserInfo()

        //handle click to go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click to pick image from camera/gallery
        binding.profileIv.setOnClickListener {
            showImageAttachedMenu()
        }

        //handle click to begin update profile
        binding.updateBtn.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private fun validateData() {
        //get data
        name = binding.nameEt.text.toString().trim()

        //validate data
        if (name.isEmpty()){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
        }
       else if (imageUri == null){
                //need to update without image
                updateProfile("")
            }
            else{
                //need to update image
                uploadImage()
            }
    }

    private fun uploadImage() {
        progressDialog.setMessage("uploading profile image")
        progressDialog.show()

        //image path and name, use uid to replace previous
        val filePathAndName = "ProfileImages/"+firebaseAuth.uid

        //storage ref
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"
                // Debugging: Log the uploaded image URL
                Log.d("UploadedImageURL", uploadedImageUrl)

                updateProfile(uploadedImageUrl)

            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Updating profile...")

        val uid = firebaseAuth.uid

//        setup info update to db
        val hashmap: HashMap<String, Any> = HashMap()
        hashmap["uid"] = "$uid"
        hashmap["name"] = "$name"

        if(imageUri != null){
            hashmap["profileImage"] = uploadedImageUrl
        }
        //update to db
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashmap)
            .addOnSuccessListener {
                //profile updated
                progressDialog.dismiss()
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                //failed to upload image
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update profile due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun loadUserInfo() {
        //db ref to load user info
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get user info
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"

                    //set data
                    binding.nameEt.setText(name)

                    //set data
                    try{
                        Glide.with(this@ProfileEditActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.baseline_person_24)
                            .into(binding.profileIv)
                    }
                    catch (e:Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun showImageAttachedMenu()
    {
//        show popup menu with options with options to pick
        val popupMenu = PopupMenu(this, binding.profileIv)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        //handle popup menu item click
        popupMenu.setOnMenuItemClickListener {item->
            //get rid of clicked item
            val id = item.itemId
            if (id == 0){
                //camera is clicked
                pickImageCamera()
            }
            else if (id == 1){
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageCamera() {
      //intent to pick image from camera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Tittle")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickImageGallery() {
       //intent to pick image from gallery
        // and returned selected item
        val galleryIntent = Intent(Intent.ACTION_PICK)
        // here item is type of image
        galleryIntent.type = "image/*"
        // ActivityResultLauncher callback
        galleryActivityResultlauncher.launch(galleryIntent)
    }
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {result ->
            //get uri of image
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data
                //set to imageview
                binding.profileIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    //used to handle result of gallery intent
    private val galleryActivityResultlauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {result ->
            //get uri of image
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data
                //set to imageview
                binding.profileIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}