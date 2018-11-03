import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;


public class PickerDebugger
{
	Connection connection;
	int entity_id;
	int is_refinement;
	int sub_panel_id;
	boolean is_member_usable_in_SP;
	Set<Integer> setProjects = new HashSet<Integer>(128);

	ResultSet selectNewSamples() throws SQLException
	{
		final String sql = "      SELECT P.project_id,\r\n"
			+ "             P.quest_feature_id,\r\n"
			+ "             PPE.sample_id,\r\n"
			+ "             SV.sample_version_id,\r\n"
			+ "             SV.sample_version_id,\r\n"
			+ "             SS.feature_id,\r\n"
			+ "             QG.quota_checking_behaviour,\r\n"
			+ "             QG.quota_group_id,\r\n"
			+ "             QG.quota_checking_level,\r\n"
			+ "             CASE WHEN ? = 0 THEN PCP.revenue_index_value ELSE PCP.refinement_index_value END AS PICKER_INDEX,\r\n"
			+ "             1\r\n"
			+ "        FROM Panel.dbo.Projects P WITH(NOLOCK)\r\n"
			+ "             INNER JOIN Panel.dbo.Quota_groups QG WITH(NOLOCK) ON QG.project_id = P.project_id\r\n"
			+ "             INNER JOIN Panel.dbo.Project_events PE WITH(NOLOCK) ON PE.quota_group_id = QG.quota_group_id\r\n"
			+ "             INNER JOIN Panel.dbo.Project_picker_events PPE WITH(NOLOCK) ON PPE.project_event_id = PE.project_event_id\r\n"
			+ "             INNER JOIN Panel.dbo.Sample_versions SV WITH(NOLOCK) ON SV.sample_id = PPE.sample_id AND SV.version_type = 13\r\n"
			+ "             INNER JOIN Panel.dbo.Picker_control_properties PCP WITH(NOLOCK) ON PCP.sample_id = PPE.sample_id\r\n"
			+ "             LEFT OUTER JOIN Panel.dbo.Picker_refinement_selections PRS WITH(NOLOCK) ON PRS.sample_version_id = SV.sample_version_id\r\n"
			+ "             LEFT OUTER JOIN Panel.dbo.Sample_selections SS WITH(NOLOCK) ON SS.sample_version_id = PRS.sample_version_id AND SS.selection_order = PRS.selection_order\r\n"
			+ "             LEFT OUTER JOIN Panel.dbo.FValues8 FV8 WITH(NOLOCK) ON FV8.feature_id = P.quest_feature_id AND FV8.entity_id = ?\r\n"
			+ "       WHERE P.entity_type_id = (SELECT entity_type_id FROM Panel.dbo.Entities ex WITH(NOLOCK) WHERE ex.entity_id = ?)\r\n"
			+ "         AND P.status NOT IN (1,3,4,5)\r\n"
			+ "         AND ISNULL(FV8.value, 0) != 2\r\n"
			+ "         AND (QG.property_1 = (SELECT ISNULL(value, -1) FROM Panel.dbo.FValues1 WHERE feature_id = (SELECT property_1_feature_id FROM Panel.dbo.Version WITH(NOLOCK)) AND entity_id = ?) OR QG.property_1 = -1)\r\n"
			+ "         AND (QG.property_2 = (SELECT ISNULL(value, -1) FROM Panel.dbo.FValues1 WHERE feature_id = (SELECT property_2_feature_id FROM Panel.dbo.Version WITH(NOLOCK)) AND entity_id = ?) OR QG.property_2 = -1)\r\n"
			+ "         AND PCP.revenue_index_value IS NOT NULL\r\n"
			+ "         AND PCP.refinement_index_value IS NOT NULL\r\n               "
			+ "         AND PCP.activation_flag = 1\r\n"
			+ "         AND (? = 0 OR (? = 1 AND PCP.is_refinement_active = 1))\r\n" + "       ORDER BY PICKER_INDEX DESC";






		final PreparedStatement statement = this.connection.prepareStatement(sql);
		statement.setInt(1, this.is_refinement);
		statement.setInt(2, this.entity_id);
		statement.setInt(3, this.entity_id);
		statement.setInt(4, this.entity_id);
		statement.setInt(5, this.entity_id);
		statement.setInt(6, this.is_refinement);
		statement.setInt(7, this.is_refinement);

		return statement.executeQuery();
	}

	void run() throws ClassNotFoundException, SQLException
	{
		this.is_refinement = 1;
		this.entity_id = 1132601240;
		this.sub_panel_id = 30;

		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		this.connection = DriverManager.getConnection(
			"jdbc:jtds:sqlserver://sqlcls.surveysampling.com/SSI;instance=Nebu;DOMAIN=SURVEYSAMPLING", "christopher_mosher",
			"P"+"h"+""+
			""+"e"+"r"+
			"f"+"s"+""+
			""+"0"+"7");

		final PreparedStatement st = this.connection.prepareStatement("USE PANEL");
		st.executeUpdate();
		st.close();

		this.is_member_usable_in_SP = is_member_usable_in_SP();

		final ResultSet rsNewSamples = selectNewSamples();
		while (rsNewSamples.next())
		{
			processSample(rsNewSamples);
		}
		rsNewSamples.getStatement().close();

		this.connection.close();
	}

	/**
	 * @param rsNewSamples
	 * @throws SQLException
	 */
	private void processSample(final ResultSet rsNewSamples) throws SQLException
	{
		Integer project_id = rsNewSamples.getInt(1);
		if (rsNewSamples.wasNull())
			project_id = null;
		Integer quest_feature_id = rsNewSamples.getInt(2);
		if (rsNewSamples.wasNull())
			quest_feature_id = null;
		Integer sample_id = rsNewSamples.getInt(3);
		if (rsNewSamples.wasNull())
			sample_id = null;
		Integer sample_version_id = rsNewSamples.getInt(4);
		if (rsNewSamples.wasNull())
			sample_version_id = null;
		// 5 is svid again
		Integer feature_id = rsNewSamples.getInt(6);
		if (rsNewSamples.wasNull())
			feature_id = null;
		Integer quota_checking_behavior = rsNewSamples.getInt(7);
		if (rsNewSamples.wasNull())
			quota_checking_behavior = null;
		Integer quota_group_id = rsNewSamples.getInt(8);
		if (rsNewSamples.wasNull())
			quota_group_id = null;
		Integer quota_checking_level = rsNewSamples.getInt(9);
		if (rsNewSamples.wasNull())
			quota_checking_level = null;
		Float picker_index = rsNewSamples.getFloat(10);
		if (rsNewSamples.wasNull())
			picker_index = null;
		Integer is_new_project = rsNewSamples.getInt(11);
		if (rsNewSamples.wasNull())
			is_new_project = null;


		if (is_new_project == null || is_new_project == 0)
		{
			throw new IllegalStateException("not new project");
		}

		System.out.println(project_id);
		System.out.println(sample_id);
		System.out.println(sample_version_id);
		System.out.println(picker_index);


		final PreparedStatement stSP = this.connection
			.prepareStatement("SELECT 1 FROM Panel.dbo.Quota_group_sub_panels WITH(NOLOCK) WHERE quota_group_id = ?");
		setInt(stSP, 1, quota_group_id);
		final ResultSet rsSP = stSP.executeQuery();
		boolean exist_SP = rsSP.next();
		stSP.close();
		System.out.println("exists SP: " + exist_SP);

		final PreparedStatement stSPG = this.connection
			.prepareStatement("SELECT 1 FROM Panel.dbo.Quota_group_sub_panel_groups WITH(NOLOCK) WHERE quota_group_id = ?");
		setInt(stSPG, 1, quota_group_id);
		final ResultSet rsSPG = stSPG.executeQuery();
		boolean exist_SPG = rsSPG.next();
		stSPG.close();
		System.out.println("exists SPG: " + exist_SPG);


		int is_sample_sub_panel_ok = 0;
		if (!exist_SP && !exist_SPG)
		{
			is_sample_sub_panel_ok = 1;
		}
		else
		{
			if (this.is_member_usable_in_SP)
			{
				final PreparedStatement stSPS = this.connection
					.prepareStatement("SELECT 1 FROM dbo.Quota_group_sub_panels WITH(NOLOCK) WHERE quota_group_id = ? AND sub_panel_id = ?");
				setInt(stSPS, 1, quota_group_id);
				setInt(stSPS, 2, this.sub_panel_id);
				final ResultSet rsSPS = stSPS.executeQuery();
				boolean exist_SPS = rsSPS.next();
				stSPS.close();
				System.out.println("exists SPS: " + exist_SPS);

				final PreparedStatement stSPGS = this.connection
					.prepareStatement("SELECT 1 FROM dbo.Quota_group_sub_panel_groups QGSPG WITH(NOLOCK)\r\n"
						+ "                                      INNER JOIN dbo.Sub_panel_groups_for_sub_panels SPGFSP WITH(NOLOCK) ON QGSPG.sub_panel_group_id = SPGFSP.sub_panel_group_id\r\n"
						+ "                                      LEFT OUTER JOIN dbo.Quota_group_excluded_sub_panels ESP WITH(NOLOCK) ON ESP.quota_group_id = QGSPG.quota_group_id AND ESP.sub_panel_group_id = QGSPG.sub_panel_group_id AND ESP.sub_panel_id = SPGFSP.sub_panel_id\r\n"
						+ "                         WHERE QGSPG.quota_group_id = ?\r\n"
						+ "                           AND SPGFSP.sub_panel_id = ?\r\n"
						+ "                           AND ESP.sub_panel_id IS NULL");
				setInt(stSPGS, 1, quota_group_id);
				setInt(stSPGS, 2, this.sub_panel_id);
				final ResultSet rsSPGS = stSPGS.executeQuery();
				boolean exist_SPGS = rsSPGS.next();
				stSPGS.close();
				System.out.println("exists SPGS: " + exist_SPGS);

				if (exist_SPS || exist_SPGS)
				{
					is_sample_sub_panel_ok = 1;
				}
			}
		}
		System.out.println("is_sample_sub_panel_ok: " + is_sample_sub_panel_ok);
		if (is_sample_sub_panel_ok != 0)
		{
			if (!project_dedupe(quota_group_id))
			{
				if (!category_dedupe(quota_group_id))
				{
					if (!this.setProjects.contains(project_id))
					{
						int soft_launch_and_running_ok = 1;
						if (!is_picker_sample_running(sample_id))
						{
							soft_launch_and_running_ok = 0;
							System.out.println("picker sample is not running");
						}
						if (is_soft_launch_reached(quota_group_id))
						{
							soft_launch_and_running_ok = 0;
							System.out.println("soft launch reached");
						}
						if (soft_launch_and_running_ok == 1)
						{
				            int already_participated = 0;
				            int member_of_the_project = 0;
				            int binded_to_the_observed_picker_sample = 0;
				            int binded_to_other_picker_sample = 0;
							final Integer quest_status = get_quest_status(quest_feature_id);
							if (quest_status != null && quest_status != 0)
							{
								System.out.println("quest status is non-zero (processing not yet implemented)");
							}
							else
							{
								
							}
						}
					}
					else
					{
						System.out.println("project already handled");
					}
				}
				else
				{
					System.out.println("DEDUPED: category");
				}
			}
			else
			{
				System.out.println("DEDUPED: project");
			}
		}

		System.out.println("----------------------------------------------------------");
	}

	private Integer get_quest_status(Integer quest_feature_id) throws SQLException
	{
		final PreparedStatement st = this.connection
		.prepareStatement("SELECT value FROM dbo.FValues8 WHERE entity_id = ? AND feature_id = ?");

		setInt(st, 1, this.entity_id);
		setInt(st, 2, quest_feature_id);
		final ResultSet rs = st.executeQuery();
		Integer is = null;
		if (rs.next())
		{
			is = rs.getInt(1);
			if (rs.wasNull()) is = null;
		}
		st.close();

		return is;
	}
	private boolean is_soft_launch_reached(Integer quota_group_id) throws SQLException
	{
		final PreparedStatement st = this.connection
			.prepareStatement("SELECT is_soft_launch_reached FROM dbo.Quota_groups WITH(NOLOCK) WHERE quota_group_id = ?");
		setInt(st, 1, quota_group_id);
		final ResultSet rs = st.executeQuery();
		int is = 0;
		while (rs.next())
		{
			is = rs.getInt(1);
		}
		st.close();

		return is == 1;
	}

	private boolean is_picker_sample_running(final Integer sample_id) throws SQLException
	{

		final PreparedStatement st = this.connection.prepareStatement("SELECT dbo.is_picker_sample_running(?)");
		setInt(st, 1, sample_id);
		final ResultSet rs = st.executeQuery();
		int is = 0;
		while (rs.next())
		{
			is = rs.getInt(1);
		}
		st.close();

		return is != 0;
	}

	private boolean category_dedupe(Integer quota_group_id) throws SQLException
	{
		final PreparedStatement st = this.connection
			.prepareStatement("SELECT 1 FROM dbo.Quota_group_category_dedupes WITH(NOLOCK) WHERE quota_group_id = ?");
		setInt(st, 1, quota_group_id);
		final ResultSet rs = st.executeQuery();
		final boolean is = rs.next();
		st.close();

		return is;
	}

	private boolean project_dedupe(Integer quota_group_id) throws SQLException
	{
		final PreparedStatement st = this.connection
			.prepareStatement("SELECT 1 FROM dbo.Quota_group_project_dedupes PD WITH(NOLOCK)\r\n"
				+ "                       INNER LOOP JOIN dbo.Quota_group_project_dedupe_projects PDP WITH(NOLOCK) ON PDP.project_dedupe_id = PD.project_dedupe_id\r\n"
				+ "                       INNER LOOP JOIN dbo.Quota_group_project_dedupe_statuses PDS WITH(NOLOCK) ON PDS.project_dedupe_id = PD.project_dedupe_id AND PDS.project_dedupe_id = PDP.project_dedupe_id\r\n"
				+ "                       INNER LOOP JOIN dbo.Projects P WITH(NOLOCK) ON P.project_id = PDP.project_id\r\n"
				+ "                       INNER HASH JOIN dbo.FValues8 FV8 WITH(NOLOCK) ON FV8.feature_id = P.quest_feature_id AND FV8.value = PDS.quest_status\r\n"
				+ "                     WHERE PD.quota_group_id = ?\r\n" + "                       AND FV8.entity_id = ?");
		setInt(st, 1, quota_group_id);
		st.setInt(2, this.entity_id);
		final ResultSet rs = st.executeQuery();
		final boolean is = rs.next();
		st.close();

		return is;
	}

	private boolean is_member_usable_in_SP() throws SQLException
	{
		final PreparedStatement st = this.connection
			.prepareStatement("	  SELECT 1 FROM dbo.Sub_panel_features SPF WITH(NOLOCK)\r\n"
				+ "                        INNER JOIN dbo.FValues1 FV1 WITH(NOLOCK) ON SPF.feature_id = FV1.feature_id\r\n"
				+ "                        INNER JOIN dbo.Membership_states MS WITH(NOLOCK) ON MS.enum_value_id = FV1.value\r\n"
				+ "                        INNER JOIN dbo.Membership_status_usability_values MSUV WITH(NOLOCK) ON MSUV.membership_status_id = MS.membership_status_id\r\n"
				+ "                      WHERE SPF.sub_panel_id = ?\r\n"
				+ "                        AND SPF.feature_order = 106\r\n"
				+ "                        AND FV1.entity_id = ?\r\n"
				+ "                        AND MSUV.usability_type_id = 2\r\n" + "");
		st.setInt(1, this.sub_panel_id);
		st.setInt(2, this.entity_id);
		final ResultSet rs = st.executeQuery();
		final boolean is = rs.next();
		st.close();

		return is;
	}

	static void setInt(PreparedStatement st, int pos, Integer i) throws SQLException
	{
		if (i == null)
			st.setNull(pos, java.sql.Types.INTEGER);
		else
			st.setInt(pos, i);
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		new PickerDebugger().run();
	}
}
