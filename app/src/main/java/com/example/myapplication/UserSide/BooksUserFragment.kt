package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.Adapters.AdapterPdfUser
import com.example.myapplication.Models.ModelPdf
import com.example.myapplication.databinding.FragmentBooksUserBinding

class BooksUserFragment : Fragment() {

    private lateinit var binding: FragmentBooksUserBinding
    private val viewModel: BooksUserViewModel by viewModels {
        BooksUserViewModelFactory(
            arguments?.getString("categoryId") ?: "",
            arguments?.getString("category") ?: "",
            arguments?.getString("uid") ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pdfList.observe(viewLifecycleOwner) { pdfList ->
            binding.booksRv.adapter = AdapterPdfUser(requireContext(),
                pdfList as ArrayList<ModelPdf>
            )
        }

        when (viewModel.category) {
            "All" -> viewModel.loadAllBooks()
            "Most Viewed" -> viewModel.loadMostDownloadedBooks("viewsCount")
            "Most Downloaded" -> viewModel.loadMostDownloadedBooks("downloadsCount")
            else -> viewModel.loadCategoryBooks()
        }
    }

    companion object {
        fun newInstance(categoryId: String, category: String, uid: String): BooksUserFragment {
            return BooksUserFragment().apply {
                arguments = Bundle().apply {
                    putString("categoryId", categoryId)
                    putString("category", category)
                    putString("uid", uid)
                }
            }
        }
    }
}
