package com.whistle.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whistle.demo.viewmodel.IssuesViewModel
import com.whistle.demo.R
import com.whistle.demo.model.Issue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssuesFragment : BaseFragment() {

    lateinit var rootView: View
    val viewModel: IssuesViewModel by activityViewModels<IssuesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.issues_fragment_title)
        rootView = LayoutInflater.from(activity).inflate(R.layout.common_recycler_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.run {
            screenState.observe(viewLifecycleOwner, Observer { setScreenState(it) })
            issues.observe(viewLifecycleOwner, Observer { showIssues(it) })
            error.observe(viewLifecycleOwner, Observer { showError(viewModel) })
        }
    }

    private fun showIssues(items: List<Issue>) {
        Toast.makeText(context,
            "Received " + items!!.size + " items", Toast.LENGTH_LONG).show()

        val recyclerView = rootView.findViewById(R.id.recycler_contents) as RecyclerView
        val adapter = IssueAdapter(items, IssueListener { issue -> startCommentFragment(issue) })
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun startCommentFragment(issue: Issue) {
        parentFragmentManager.commit {
            val fragment = CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(CommentsFragment.COMMENTS_URL, issue.commentsUrl)
                }
            }
            replace(R.id.fragment_container, fragment, fragment::class.java.simpleName)
            addToBackStack(fragment::class.java.simpleName)
        }
    }
}