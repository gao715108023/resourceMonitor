package com.sinopec.dao;

import com.sinopec.bean.ComputeNodeInfoBean;
import com.sinopec.bean.ComputeNodeStatusBean;
import com.sinopec.bean.NodeInfoSampleConfigureBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-25 Time: 下午2:18 To
 * change this template use File | Settings | File Templates.
 */
public interface MsgDao {

	public List<NodeInfoSampleConfigureBean> selectNodeInfoSampleConfigureBean(String nodeName);

	public int insertComputeNodeStatus(ComputeNodeStatusBean computeNodeStatusBean);

	public List<ComputeNodeStatusBean> selectComputeNodeStatus(String nodeName);

	public int updateComputeNodeStatus(String nodeName, ComputeNodeStatusBean computeNodeStatusBean);

	public int insertComputeNodeStatusOneMonthHis(ComputeNodeStatusBean computeNodeStatusBean);

	public int insertComputeNodeStatusThreeMonthHis(ComputeNodeStatusBean computeNodeStatusBean);

	public int insertComputeNodeInfo(ComputeNodeInfoBean computeNodeInfoBean);

	public List<ComputeNodeInfoBean> selectComputeNodeInfo(String nodeName);

	public int updateComputeNodeInfo(String nodeName, ComputeNodeInfoBean computeNodeInfoBean);
}
