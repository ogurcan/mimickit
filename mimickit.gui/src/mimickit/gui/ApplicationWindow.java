package mimickit.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mimickit.SOEASYController;
import mimickit.model.MNDischargeRates;
import mimickit.model.SOEASYEnvironment;
import mimickit.model.SOEASYParameters;
import mimickit.scenario.singlemotorunit.ScenarioExecuter;
import mimickit.util.Correlation;
import mimickit.util.CuSum;
import mimickit.util.Reflex;
import mimickit.util.StaticPSF;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class ApplicationWindow {

	private JFrame frmSoeasyWorkbench;
	private JTextField textField;
	private JTextField tfAverageInterval;
	private JTextField tfFaultTolerance;
	private JTextField textField_3;
	private JTextField tfCurrentTick;

	private JButton btnRunPause;
	private JButton btnStop;
	private JButton btnBrowse;
	private JTextField tfInfo;

	private JTabbedPane tabbedPane;

	private PSFPanel panelReferencePSF;
	private PSFPanel panelReferencePSFPreview;
	private PSFPanel panelSimulatedPSF;
	private PSFPanel panelPSFCombined;
	//
	private PSFCUSUMPanel panelSettingsRefPSFCUSUM;
	private PSFCUSUMPanel panelReferencePSFCUSUM;
	private PSFCUSUMPanel panelSimulatedPSFCUSUM;
	private PSFCUSUMPanel panelPSFCUSUMCombined;
	//
	private PSFCUSUMDerivativePanel panelReferencePSFCUSUMDerivative;
	private PSFCUSUMDerivativePanel panelReferencePSFCUSUMDerivativePreview;
	private PSFCUSUMDerivativePanel panelSimulatedPSFCUSUMDerivative;
	private PSFCUSUMDerivativePanel panelPSFCUSUMDerivativeCombined;
	//
	private PSTHPanel panelRefPSTH;
	private PSTHPanel panelSimulatedPSTH;
	private PSTHPanel panelPSTHCombined;

	private PSTHCUSUMPanel panelReferencePSTHCUSUM;
	private PSTHCUSUMPanel panelSimulatedPSTHCUSUM;
	private PSTHCUSUMPanel panelPSTHCUSUMCombined;

	private PSTHCUSUMDerivativePanel panelRefPSTHCUSUMDerivative;
	private PSTHCUSUMDerivativePanel panelSimulatedPSTHCUSUMDerivative;
	private PSTHCUSUMDerivativePanel panelPSTHCUSUMDerivativeCombined;

	private HistogramChartPanel panelReferencePSFEmptyBinsHistogram;
	private JTextField tfPearsonCorrelation;
	private NeuronEvolutionPanel panelNeuronsByTime;
	private ViewersLastFeedbacksForMN panelViewersLastFeedbacks;
	private SynapseEvolutionPanel panelSynapsesByTime;
	private CriticalityPanel panelCriticality;
	private HistogramChartPanel panelCriticalityDistribution;
	private HistogramChartPanel panelSynapsesOnMN;
	private LineChartPanel panelEffectOnMN;
	private FeedbacksEvolutionPanel panelViewersFeedbackEvolution;

	private JTextField tfSimilarity;

	private JTextField tfPearsonCorrelation4Average;
	private JTextField tfSimilarity4Average;
	private JTextField tfDiagramRangeFrom;
	private JTextField tfDiagramRangeTo;
	private JCheckBox chckbxAutoAdjustTimeRange;
	private JTextField tfPathwayBeginning;
	private JTextField tfPathwayEnd;
	private JTextField tfCurrentTime;
	private JTextField tfNumberOfNeurons;
	private JTextField tfNumberOfSynapses;
	private JTextField textField_1;
	private JTextField tfConsecutiveEmptyBins;
	private JTextField tfTriggerDataFile;
	private JTextField tfMNDischargesDataFile;
	private JTextField tfPSFCUSUMSimilarity;
	private JTextField tfViewerGoodRate;

	private JPanel panelViewerAgent;

	private String fileSeparator = System.getProperty("file.separator");
	private JTextField textField_5;
	private JTextField textField_4;
	private JTextField tfTriggerCount;

	private String experimentFolderName = "";
	private JTextField tfPrestimulusMean;
	private JTextField tfPrestimulusStdDev;
	private JTextField tfPSTHCUSUMPearsonCorrelation;
	private JTextField tfPSTHCUSUMSimilarity;
	private JPanel panelPSFDiagrams;
	private JPanel panelPSTHDiagrams;	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					RefineryUtilities
							.centerFrameOnScreen(window.frmSoeasyWorkbench);
					window.frmSoeasyWorkbench.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSoeasyWorkbench = new JFrame();
		frmSoeasyWorkbench.setTitle("SO-EASY - Demo Version");
		frmSoeasyWorkbench.setBounds(100, 100, 1006, 601);
		frmSoeasyWorkbench.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmSoeasyWorkbench.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.setEnabled(false);
		mnFile.add(mntmOpen);

		final JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(
						"../SO-EASY-DATA/simulated/"));
				chooser.setDialogTitle("Choose a folder for storing your experiment");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showSaveDialog(frmSoeasyWorkbench) == JFileChooser.APPROVE_OPTION) {
					String folder = chooser.getSelectedFile().getAbsoluteFile()
							.toString();
					SOEASYController controller = SOEASYController
							.getInstance();
					SOEASYParameters parameters = SOEASYParameters
							.getInstance();
					SOEASYEnvironment environment = SOEASYEnvironment
							.getInstance();
					// export the network as graphML
					controller.exportAsGraphML(folder);
					try {
						// export the parameters to a file
						parameters.saveToFile(folder + "/parameters.txt");

						// export the results to a file
						controller.saveToFile(folder + "/results.txt");

						StaticPSF simulatedPSF = environment.getDataSet().getSimulatedPSF();
						simulatedPSF.getTriggers()
								.writeToFile(folder + "/sim-trigger.txt");
						simulatedPSF.getMnDischarges()
								.writeToFile(folder + "/sim-mndischarge.txt");

						// export the diagrams as png files
						int width = 613;
						int height = 395;

						// export PSF diagrams
						panelPSFCombined.saveChartAsPNG(new File(folder
								+ "/combined-psf.png"), width, height);
						Range psfRange = panelPSFCombined.getRangeAxisRange();
						//
						panelReferencePSF.setRangeAxisRange(psfRange);
						panelReferencePSF.saveChartAsPNG(new File(folder
								+ "/ref-psf.png"), width, height);
						//
						panelSimulatedPSF.setRangeAxisRange(psfRange);
						panelSimulatedPSF.saveChartAsPNG(new File(folder
								+ "/sim-psf.png"), width, height);

						// export PSF-CUSUM derivative diagrams
						panelPSFCUSUMDerivativeCombined
								.saveChartAsPNG(new File(folder
										+ "/combined-psf-cusum-drv.png"),
										width, height);
						Range derivativeRange = panelPSFCUSUMDerivativeCombined
								.getRangeAxisRange();
						//
						panelReferencePSFCUSUMDerivative
								.setRangeAxisRange(derivativeRange);
						panelReferencePSFCUSUMDerivative.saveChartAsPNG(
								new File(folder + "/ref-psf-cusum-drv.png"),
								width, height);
						//
						panelSimulatedPSFCUSUMDerivative
								.setRangeAxisRange(derivativeRange);
						panelSimulatedPSFCUSUMDerivative.saveChartAsPNG(
								new File(folder + "/sim-psf-cusum-drv.png"),
								width, height);

						// export PSF-CUSUM diagrams
						panelPSFCUSUMCombined.saveChartAsPNG(new File(folder
								+ "/combined-psf-cusum.png"), width, height);
						Range cusumRange = panelPSFCUSUMCombined
								.getRangeAxisRange();
						//
						panelReferencePSFCUSUM.setRangeAxisRange(cusumRange);
						panelReferencePSFCUSUM.saveChartAsPNG(new File(folder
								+ "/ref-psf-cusum.png"), width, height);
						//
						panelSimulatedPSFCUSUM.setRangeAxisRange(cusumRange);
						panelSimulatedPSFCUSUM.saveChartAsPNG(new File(folder
								+ "/sim-psf-cusum.png"), width, height);

						// export PSTH diagrams
						panelPSTHCombined.saveChartAsPNG(new File(folder
								+ "/combined-psth-cusum.png"), width, height);
						Range psthRange = panelPSTHCombined.getRangeAxisRange();
						
						panelRefPSTH.setRangeAxisRange(psthRange);
						panelRefPSTH.saveChartAsPNG(new File(folder
								+ "/ref-psth.png"), width, height);
						//
						panelSimulatedPSTH.setRangeAxisRange(psthRange);
						panelSimulatedPSTH.saveChartAsPNG(new File(folder
								+ "/sim-psth.png"), width, height);

						// export PSTH-CUSUM diagrams
						 panelPSTHCUSUMCombined.saveChartAsPNG(new File(folder
						 + "/combined-psth-cusum.png"), width, height);
						 cusumRange = panelPSTHCUSUMCombined.getRangeAxisRange();
						//
						panelReferencePSTHCUSUM.setRangeAxisRange(cusumRange);
						panelReferencePSTHCUSUM.saveChartAsPNG(new File(folder
								+ "/ref-psth-cusum.png"), width, height);
						//
						panelSimulatedPSTHCUSUM.setRangeAxisRange(cusumRange);
						panelSimulatedPSTHCUSUM.saveChartAsPNG(new File(folder
								+ "/sim-psth-cusum.png"), width, height);

						// export neurons by time
						panelNeuronsByTime.saveChartAsPNG(new File(folder
								+ "/neurons-by-time.png"), width, height);
						//
						panelSynapsesByTime.saveChartAsPNG(new File(folder
								+ "/synapses-by-time.png"), width, height);
						//
						panelViewersLastFeedbacks.saveChartAsPNG(new File(
								folder + "/viewers-last-feedbacks.png"), width,
								height);
						//
						panelSynapsesOnMN
								.saveChartAsPNG(new File(folder
										+ "/synapses-on-motoneuron.png"),
										width, height);
						//
						panelEffectOnMN.saveChartAsPNG(new File(folder
								+ "/net-effect-on-motoneuron.png"), width,
								height);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		mntmSaveAs.setEnabled(true);
		mnFile.add(mntmSaveAs);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmExit.setIcon(null);
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JPanel panelInfo = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelInfo.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		panelInfo.setBorder(new CompoundBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), new BevelBorder(
				BevelBorder.LOWERED, null, null, null, null)));
		frmSoeasyWorkbench.getContentPane().add(panelInfo, BorderLayout.SOUTH);

		tfInfo = new JTextField();
		tfInfo.setFont(new Font("Tahoma", Font.BOLD, 10));
		tfInfo.setBackground(UIManager.getColor("Panel.background"));
		tfInfo.setText("Welcome to SO-EASY Demo Version");
		tfInfo.setEditable(false);
		panelInfo.add(tfInfo);
		tfInfo.setColumns(30);
		tfInfo.setBorder(null);

		JToolBar toolBar = new JToolBar();
		frmSoeasyWorkbench.getContentPane().add(toolBar, BorderLayout.NORTH);

		JPanel panelCurrentTick = new JPanel();
		toolBar.add(panelCurrentTick);
		panelCurrentTick.setLayout(new BorderLayout(0, 0));

		JPanel panelSimulationSummary = new JPanel();
		panelCurrentTick.add(panelSimulationSummary);
		panelSimulationSummary.setLayout(new FlowLayout(FlowLayout.TRAILING, 5,
				5));

		JPanel panelNumberOfElements = new JPanel();
		panelSimulationSummary.add(panelNumberOfElements);
		panelNumberOfElements.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelNumberOfNeurons = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panelNumberOfNeurons.getLayout();
		flowLayout_4.setAlignment(FlowLayout.TRAILING);
		panelNumberOfElements.add(panelNumberOfNeurons);

		JLabel lblNumberOfNeurons = new JLabel("Number of Neurons:");
		lblNumberOfNeurons.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelNumberOfNeurons.add(lblNumberOfNeurons);

		tfNumberOfNeurons = new JTextField();
		tfNumberOfNeurons.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfNumberOfNeurons.setText("2");
		tfNumberOfNeurons.setHorizontalAlignment(SwingConstants.TRAILING);
		tfNumberOfNeurons.setEditable(false);
		tfNumberOfNeurons.setBorder(null);
		panelNumberOfNeurons.add(tfNumberOfNeurons);
		tfNumberOfNeurons.setColumns(5);

		JPanel panelNumberOfSynapses = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) panelNumberOfSynapses
				.getLayout();
		flowLayout_5.setAlignment(FlowLayout.TRAILING);
		panelNumberOfElements.add(panelNumberOfSynapses);

		JLabel lblNumberOfSynapses = new JLabel("Number of Synapses:");
		lblNumberOfSynapses.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelNumberOfSynapses.add(lblNumberOfSynapses);

		tfNumberOfSynapses = new JTextField();
		tfNumberOfSynapses.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfNumberOfSynapses.setText("1");
		tfNumberOfSynapses.setHorizontalAlignment(SwingConstants.TRAILING);
		tfNumberOfSynapses.setEditable(false);
		tfNumberOfSynapses.setBorder(null);
		panelNumberOfSynapses.add(tfNumberOfSynapses);
		tfNumberOfSynapses.setColumns(5);

		JPanel panel_2 = new JPanel();
		panelSimulationSummary.add(panel_2);

		JPanel panelConvergence = new JPanel();
		panelSimulationSummary.add(panelConvergence);
		panelConvergence.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelPSFCUSUMSimilarity = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) panelPSFCUSUMSimilarity
				.getLayout();
		flowLayout_7.setAlignment(FlowLayout.TRAILING);
		panelConvergence.add(panelPSFCUSUMSimilarity);

		JLabel lblNewLabel_6 = new JLabel("PSF-CUSUM Similarity:");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelPSFCUSUMSimilarity.add(lblNewLabel_6);

		tfPSFCUSUMSimilarity = new JTextField();
		tfPSFCUSUMSimilarity.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfPSFCUSUMSimilarity.setText("-");
		tfPSFCUSUMSimilarity.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPSFCUSUMSimilarity.setEditable(false);
		tfPSFCUSUMSimilarity.setBorder(null);
		panelPSFCUSUMSimilarity.add(tfPSFCUSUMSimilarity);
		tfPSFCUSUMSimilarity.setColumns(6);

		JPanel panelGoodFeedbackRate = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) panelGoodFeedbackRate
				.getLayout();
		flowLayout_6.setAlignment(FlowLayout.TRAILING);
		panelConvergence.add(panelGoodFeedbackRate);

		JLabel lblViewersGoodRate = new JLabel("Viewer's Good Rate:");
		lblViewersGoodRate.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelGoodFeedbackRate.add(lblViewersGoodRate);

		tfViewerGoodRate = new JTextField();
		tfViewerGoodRate.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfViewerGoodRate.setHorizontalAlignment(SwingConstants.TRAILING);
		tfViewerGoodRate.setText("-");
		tfViewerGoodRate.setEditable(false);
		tfViewerGoodRate.setBorder(null);
		panelGoodFeedbackRate.add(tfViewerGoodRate);
		tfViewerGoodRate.setColumns(6);

		JPanel panel_3 = new JPanel();
		panelSimulationSummary.add(panel_3);

		JPanel panelTime = new JPanel();
		panelSimulationSummary.add(panelTime);
		panelTime.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelSimulationTime = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelSimulationTime.getLayout();
		flowLayout_2.setAlignment(FlowLayout.TRAILING);
		panelTime.add(panelSimulationTime);

		JLabel lblTimeMs = new JLabel("Simulation Time: ");
		panelSimulationTime.add(lblTimeMs);
		lblTimeMs.setFont(new Font("Tahoma", Font.BOLD, 11));

		tfCurrentTick = new JTextField();
		panelSimulationTime.add(tfCurrentTick);
		tfCurrentTick.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfCurrentTick.setEditable(false);
		tfCurrentTick.setHorizontalAlignment(SwingConstants.TRAILING);
		tfCurrentTick.setText("0.0 ms");
		tfCurrentTick.setBorder(null);
		tfCurrentTick.setColumns(12);

		JPanel panelRealTime = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panelRealTime.getLayout();
		flowLayout_3.setAlignment(FlowLayout.TRAILING);
		panelTime.add(panelRealTime);

		JLabel lblNewLabel_13 = new JLabel("Real Time: ");
		lblNewLabel_13.setFont(new Font("Tahoma", Font.BOLD, 11));
		panelRealTime.add(lblNewLabel_13);

		tfCurrentTime = new JTextField();
		tfCurrentTime.setHorizontalAlignment(SwingConstants.TRAILING);
		tfCurrentTime.setText("0.0 ms");
		tfCurrentTime.setFont(new Font("Tahoma", Font.BOLD, 11));
		tfCurrentTime.setEditable(false);
		panelRealTime.add(tfCurrentTime);
		tfCurrentTime.setBorder(null);
		tfCurrentTime.setColumns(12);

		JPanel panelToolbar = new JPanel();
		panelCurrentTick.add(panelToolbar, BorderLayout.WEST);

		JButton btnSaveAs = new JButton("");
		panelToolbar.add(btnSaveAs);
		btnSaveAs.setEnabled(false);
		btnSaveAs.setIcon(new ImageIcon("./icons/24x24/gtk-save-as.png"));
		btnSaveAs.setHorizontalAlignment(SwingConstants.LEFT);

		btnRunPause = new JButton("");
		panelToolbar.add(btnRunPause);
		btnRunPause.setEnabled(false);

		btnRunPause.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (!btnRunPause.isSelected()) { // run button clicked
					SOEASYParameters parameters = SOEASYController
							.getInstance().getParameters();

					String packageName = ApplicationWindow.class.getPackage()
							.getName();
					String reportsFolder = "./src/"
							+ packageName.replace('.', '/') + "/data/";

					parameters.setReportsFolder(reportsFolder);

					SOEASYController.getInstance().runSimulation(
							new ScenarioExecuter());

					btnBrowse.setEnabled(false);
					btnRunPause.setEnabled(false);
					btnStop.setEnabled(true);
					tfInfo.setText("Simulation is running for experiment "
							+ experimentFolderName + "...");
					tabbedPane.setSelectedIndex(1);

					Timer timer = new Timer();
					timer.schedule(new RefreshingTask(), 2000, 2000);
				} else { // pause button clicked

				}
			}
		});
		btnRunPause.setSelectedIcon(null);
		btnRunPause.setIcon(new ImageIcon(
				"./icons/24x24/gtk-media-play-ltr.png"));

		btnStop = new JButton("");
		panelToolbar.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SOEASYController.getInstance().stopSimulation();
				btnRunPause.setEnabled(false);
				btnStop.setEnabled(false);
				tfInfo.setText("Simulation is stopped.");
				mntmSaveAs.setEnabled(true);
			}
		});
		btnStop.setEnabled(false);
		btnStop.setIcon(new ImageIcon("./icons/24x24/gtk-media-stop.png"));

		final JButton btnStopFeedbacks = new JButton("ON");
		btnStopFeedbacks.setBackground(Color.GREEN);
		btnStopFeedbacks.setForeground(Color.BLACK);
		btnStopFeedbacks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isSendingFeedback = SOEASYParameters.getInstance()
						.isSendingFeedback();
				if (isSendingFeedback) {
					SOEASYParameters.getInstance().setSendingFeedback(false);
					btnStopFeedbacks.setBackground(Color.RED);
					btnStopFeedbacks.setText("OFF");
				} else {
					SOEASYParameters.getInstance().setSendingFeedback(true);
					btnStopFeedbacks.setBackground(Color.GREEN);
					btnStopFeedbacks.setText("ON");
				}
			}
		});
		panelToolbar.add(btnStopFeedbacks);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmSoeasyWorkbench.getContentPane()
				.add(tabbedPane, BorderLayout.CENTER);

		JTabbedPane tabbedPaneSettings = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Settings", new ImageIcon(
				"./icons/24x24/gtk-preferences.png"), tabbedPaneSettings, null);

		panelViewerAgent = new JPanel();
		tabbedPaneSettings.addTab("Viewer Agent", null, panelViewerAgent, null);
		panelViewerAgent.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panelReferenceData = new JPanel();
		panelViewerAgent.add(panelReferenceData);
		panelReferenceData.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Reference Data",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelReferenceData.setLayout(new BoxLayout(panelReferenceData,
				BoxLayout.Y_AXIS));

		JPanel panelReferenceFiles = new JPanel();
		panelReferenceFiles.setBorder(new TitledBorder(null, "Files",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelReferenceData.add(panelReferenceFiles);
		panelReferenceFiles.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panelReferenceFiles.add(panel_1, BorderLayout.NORTH);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel lblTriggerFile = new JLabel("Trigger file:");
		GridBagConstraints gbc_lblTriggerFile = new GridBagConstraints();
		gbc_lblTriggerFile.anchor = GridBagConstraints.EAST;
		gbc_lblTriggerFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblTriggerFile.gridx = 0;
		gbc_lblTriggerFile.gridy = 0;
		panel_1.add(lblTriggerFile, gbc_lblTriggerFile);

		tfTriggerDataFile = new JTextField();
		tfTriggerDataFile.setEditable(false);
		GridBagConstraints gbc_tfTriggerDataFile = new GridBagConstraints();
		gbc_tfTriggerDataFile.insets = new Insets(0, 0, 5, 5);
		gbc_tfTriggerDataFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTriggerDataFile.gridx = 1;
		gbc_tfTriggerDataFile.gridy = 0;
		panel_1.add(tfTriggerDataFile, gbc_tfTriggerDataFile);
		tfTriggerDataFile.setColumns(10);

		JLabel lblMnDischargeFile = new JLabel("MN Discharge file:");
		GridBagConstraints gbc_lblMnDischargeFile = new GridBagConstraints();
		gbc_lblMnDischargeFile.anchor = GridBagConstraints.EAST;
		gbc_lblMnDischargeFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblMnDischargeFile.gridx = 0;
		gbc_lblMnDischargeFile.gridy = 1;
		panel_1.add(lblMnDischargeFile, gbc_lblMnDischargeFile);

		tfMNDischargesDataFile = new JTextField();
		tfMNDischargesDataFile.setEditable(false);
		GridBagConstraints gbc_tfMNDischargesDataFile = new GridBagConstraints();
		gbc_tfMNDischargesDataFile.insets = new Insets(0, 0, 5, 5);
		gbc_tfMNDischargesDataFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfMNDischargesDataFile.gridx = 1;
		gbc_tfMNDischargesDataFile.gridy = 1;
		panel_1.add(tfMNDischargesDataFile, gbc_tfMNDischargesDataFile);
		tfMNDischargesDataFile.setColumns(10);

		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(
						"../SO-EASY-DATA/experimental/"));
				chooser.setDialogTitle("Choose an experimental data folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(frmSoeasyWorkbench) == JFileChooser.APPROVE_OPTION) {
					String folder = chooser.getSelectedFile().getAbsoluteFile()
							.toString();
					File triggerFile = new File(folder + fileSeparator
							+ "trigger.txt");
					File mnDischargesFile = new File(folder + fileSeparator
							+ "mndischarge.txt");
					if (triggerFile.exists() && mnDischargesFile.exists()) {
						SOEASYParameters parameters = SOEASYController
								.getInstance().getParameters();
						//
						String triggerFileName = triggerFile.getAbsolutePath();
						String mnDischargesFileName = mnDischargesFile
								.getAbsolutePath();
						MNDischargeRates dataSet = SOEASYController.getInstance().createDataSet(triggerFileName, mnDischargesFileName);
						//
						int triggerCount = dataSet.getReferenceTriggerCount();
						tfTriggerCount.setText("" + triggerCount);

						StaticPSF referencePSF = dataSet.getReferencePSF();
						tfPrestimulusMean.setText(""
								+ referencePSF
										.getMeanPrestimulusDischargeRate());

						tfPrestimulusStdDev.setText(""
								+ referencePSF
										.getPrestimulusStandartDeviation());
						//
						generateReferenceCharts();
						//
						btnRunPause.setEnabled(true);
						chckbxAutoAdjustTimeRange.setEnabled(true);
						//
						Reflex hReflex = dataSet.getReferencePSTH().getHReflex();
						double hReflexLatency = 40;
						if (hReflex != null) {
							hReflexLatency = hReflex.getLatency() - 4.0;
						}
						parameters.setParameter(
								SOEASYParameters.PATHWAY_BEGINNING, ""
										+ hReflexLatency);
						tfPathwayBeginning.setText("" + hReflexLatency);
						panelReferencePSFPreview.updateTarget();
						panelReferencePSFCUSUMDerivativePreview.updateTarget();
						//
						parameters.toString();

						experimentFolderName = chooser.getSelectedFile()
								.getName().toString();

					} else {
						String message = "Invalid folder! The folder you chose must contain the 'trigger.txt' and the 'mndischarges.txt' files.";
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								message);
					}
				}
			}
		});
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.anchor = GridBagConstraints.CENTER;
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridheight = 2;
		gbc_btnBrowse.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBrowse.gridx = 2;
		gbc_btnBrowse.gridy = 0;
		panel_1.add(btnBrowse, gbc_btnBrowse);

		JPanel panelCenter00 = new JPanel();
		panelReferenceData.add(panelCenter00);
		panelCenter00.setLayout(new BorderLayout(0, 0));

		JPanel panelPSFSettings = new JPanel();
		panelPSFSettings.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "PSF Properties",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelCenter00.add(panelPSFSettings, BorderLayout.NORTH);
		GridBagLayout gbl_panelPSFSettings = new GridBagLayout();
		gbl_panelPSFSettings.columnWidths = new int[] { 151, 151, 0 };
		gbl_panelPSFSettings.rowHeights = new int[] { 23, 0, 0, 0, 0, 0 };
		gbl_panelPSFSettings.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_panelPSFSettings.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		panelPSFSettings.setLayout(gbl_panelPSFSettings);

		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Digitalize (ms)");
		chckbxNewCheckBox_2.setSelected(true);
		GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_2.fill = GridBagConstraints.BOTH;
		gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_2.gridx = 0;
		gbc_chckbxNewCheckBox_2.gridy = 0;
		panelPSFSettings.add(chckbxNewCheckBox_2, gbc_chckbxNewCheckBox_2);

		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.TRAILING);
		textField_1.setText("0.5");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		panelPSFSettings.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		JCheckBox chckbxNewCheckBox_3 = new JCheckBox(
				"Filter consequetive empty bins");
		chckbxNewCheckBox_3.setSelected(true);
		GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_3.fill = GridBagConstraints.BOTH;
		gbc_chckbxNewCheckBox_3.gridx = 0;
		gbc_chckbxNewCheckBox_3.gridy = 1;
		panelPSFSettings.add(chckbxNewCheckBox_3, gbc_chckbxNewCheckBox_3);

		tfConsecutiveEmptyBins = new JTextField();
		tfConsecutiveEmptyBins.setText("3");
		tfConsecutiveEmptyBins.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_tfConsecutiveEmptyBins = new GridBagConstraints();
		gbc_tfConsecutiveEmptyBins.insets = new Insets(0, 0, 5, 0);
		gbc_tfConsecutiveEmptyBins.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfConsecutiveEmptyBins.gridx = 1;
		gbc_tfConsecutiveEmptyBins.gridy = 1;
		panelPSFSettings
				.add(tfConsecutiveEmptyBins, gbc_tfConsecutiveEmptyBins);
		tfConsecutiveEmptyBins.setColumns(10);

		JLabel lblTriggerCount_1 = new JLabel("Trigger count");
		lblTriggerCount_1.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblTriggerCount_1 = new GridBagConstraints();
		gbc_lblTriggerCount_1.anchor = GridBagConstraints.WEST;
		gbc_lblTriggerCount_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblTriggerCount_1.gridx = 0;
		gbc_lblTriggerCount_1.gridy = 2;
		panelPSFSettings.add(lblTriggerCount_1, gbc_lblTriggerCount_1);

		tfTriggerCount = new JTextField();
		tfTriggerCount.setEditable(false);
		tfTriggerCount.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_tfTriggerCount = new GridBagConstraints();
		gbc_tfTriggerCount.insets = new Insets(0, 0, 5, 0);
		gbc_tfTriggerCount.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTriggerCount.gridx = 1;
		gbc_tfTriggerCount.gridy = 2;
		panelPSFSettings.add(tfTriggerCount, gbc_tfTriggerCount);
		tfTriggerCount.setColumns(10);

		JLabel lblNewLabel_12 = new JLabel("Prestimulus Mean");
		lblNewLabel_12.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_12.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_12.gridx = 0;
		gbc_lblNewLabel_12.gridy = 3;
		panelPSFSettings.add(lblNewLabel_12, gbc_lblNewLabel_12);

		tfPrestimulusMean = new JTextField();
		tfPrestimulusMean.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPrestimulusMean.setEditable(false);
		GridBagConstraints gbc_tfPrestimulusMean = new GridBagConstraints();
		gbc_tfPrestimulusMean.insets = new Insets(0, 0, 5, 0);
		gbc_tfPrestimulusMean.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPrestimulusMean.gridx = 1;
		gbc_tfPrestimulusMean.gridy = 3;
		panelPSFSettings.add(tfPrestimulusMean, gbc_tfPrestimulusMean);
		tfPrestimulusMean.setColumns(10);

		JLabel lblNewLabel_14 = new JLabel("Prestimulus StdDev");
		GridBagConstraints gbc_lblNewLabel_14 = new GridBagConstraints();
		gbc_lblNewLabel_14.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_14.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_14.gridx = 0;
		gbc_lblNewLabel_14.gridy = 4;
		panelPSFSettings.add(lblNewLabel_14, gbc_lblNewLabel_14);

		tfPrestimulusStdDev = new JTextField();
		tfPrestimulusStdDev.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPrestimulusStdDev.setEditable(false);
		GridBagConstraints gbc_tfPrestimulusStdDev = new GridBagConstraints();
		gbc_tfPrestimulusStdDev.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPrestimulusStdDev.gridx = 1;
		gbc_tfPrestimulusStdDev.gridy = 4;
		panelPSFSettings.add(tfPrestimulusStdDev, gbc_tfPrestimulusStdDev);
		tfPrestimulusStdDev.setColumns(10);

		JPanel panelCenter01 = new JPanel();
		panelReferenceData.add(panelCenter01);
		panelCenter01.setLayout(new BorderLayout(0, 0));

		JPanel panelViewer = new JPanel();
		panelCenter01.add(panelViewer, BorderLayout.NORTH);
		panelViewer.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"),
				"PSF-CUSUM Derivative Settings", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panelViewer.setLayout(new BorderLayout(0, 0));

		JPanel panelAverageParameters = new JPanel();
		panelViewer.add(panelAverageParameters);
		panelAverageParameters.setLayout(new GridLayout(3, 3, 0, 0));

		JLabel lblMovingAverageInterval = new JLabel("Average interval: ");
		lblMovingAverageInterval
				.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAverageParameters.add(lblMovingAverageInterval);

		tfAverageInterval = new JTextField();
		tfAverageInterval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String text = tfAverageInterval.getText();
					double value = Double.parseDouble(text);
					if (value <= 0.0) {
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								"The value should be higher than 0.0.",
								"Invalid Interval Value",
								JOptionPane.ERROR_MESSAGE);
					}
					tfAverageInterval.setText(String.valueOf(value));
					MNDischargeRates dataSet = SOEASYController
							.getInstance().getDataSet();
					dataSet.setMovingAverageInterval(value);

					generateReferenceCharts();
				} catch (Exception e) {
					JOptionPane
							.showMessageDialog(
									ApplicationWindow.this.frmSoeasyWorkbench,
									"Please enter a real number value.",
									"Invalid Interval Value",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		tfAverageInterval.setHorizontalAlignment(SwingConstants.TRAILING);
		tfAverageInterval.setText("4.0");
		panelAverageParameters.add(tfAverageInterval);
		tfAverageInterval.setColumns(10);

		JLabel lblEmptyLabel_1 = new JLabel("");
		panelAverageParameters.add(lblEmptyLabel_1);

		JLabel lblSmoot = new JLabel("Smooth: ");
		lblSmoot.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAverageParameters.add(lblSmoot);

		final JSpinner spinnerSmooth = new JSpinner();
		spinnerSmooth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int smooth = Integer.parseInt(spinnerSmooth.getValue().toString());
				MNDischargeRates dataSet = SOEASYEnvironment.getInstance().getDataSet();
				dataSet.setSmoothingCount(smooth);				
				generateReferenceCharts();
			}
		});
		spinnerSmooth.setModel(new SpinnerNumberModel(new Integer(1), null,
				null, new Integer(1)));
		panelAverageParameters.add(spinnerSmooth);

		JLabel lblEmptyLabel_2 = new JLabel("");
		panelAverageParameters.add(lblEmptyLabel_2);

		JLabel lblNewLabel = new JLabel("Fault tolerance (stddev): ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAverageParameters.add(lblNewLabel);

		tfFaultTolerance = new JTextField();
		tfFaultTolerance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String text = tfFaultTolerance.getText();
					double value = Double.parseDouble(text);
					if (value <= 0.0) {
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								"The value should be higher than 0.0.",
								"Invalid Interval Value",
								JOptionPane.ERROR_MESSAGE);
					}
					tfFaultTolerance.setText(String.valueOf(value));
					SOEASYParameters parameters = SOEASYController
							.getInstance().getParameters();
					parameters.setParameter(
							SOEASYParameters.DERIVATIVE_FAULT_TOLERANCE,
							tfFaultTolerance.getText());

					generateReferenceCharts();
				} catch (Exception e) {
					JOptionPane
							.showMessageDialog(
									ApplicationWindow.this.frmSoeasyWorkbench,
									"Please enter a real number value.",
									"Invalid Interval Value",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		tfFaultTolerance.setText("0.50");
		tfFaultTolerance.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAverageParameters.add(tfFaultTolerance);
		tfFaultTolerance.setColumns(10);

		JPanel panelCenter04 = new JPanel();
		panelReferenceData.add(panelCenter04);
		panelCenter04.setLayout(new BorderLayout(0, 0));

		JPanel panelStimulus = new JPanel();
		panelStimulus.setBorder(new TitledBorder(null, "Stimulus",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelCenter04.add(panelStimulus);
		GridBagLayout gbl_panelStimulus = new GridBagLayout();
		gbl_panelStimulus.columnWidths = new int[] { 151, 0, 151 };
		gbl_panelStimulus.rowHeights = new int[] { 23, 0 };
		gbl_panelStimulus.columnWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_panelStimulus.rowWeights = new double[] { 0.0, 0.0 };
		panelStimulus.setLayout(gbl_panelStimulus);

		JLabel lblNewLabel_8 = new JLabel("Stimulus Occurance");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 0;
		panelStimulus.add(lblNewLabel_8, gbc_lblNewLabel_8);

		textField_5 = new JTextField();
		textField_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String text = textField_5.getText();
					int occurance = Integer.parseInt(text);
					if (occurance <= 0) {
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								"The value should be higher than 0 (zero).",
								"Invalid Interval Value",
								JOptionPane.ERROR_MESSAGE);
					}
					textField_5.setText(String.valueOf(occurance));
					SOEASYParameters parameters = SOEASYController
							.getInstance().getParameters();
					parameters.setParameter(
							SOEASYParameters.STIMULUS_OCCURANCE,
							textField_5.getText());
				} catch (Exception e) {
					JOptionPane
							.showMessageDialog(
									ApplicationWindow.this.frmSoeasyWorkbench,
									"Please enter a integer number value.",
									"Invalid Interval Value",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		textField_5.setText("1");
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.insets = new Insets(0, 0, 5, 0);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 1;
		gbc_textField_5.gridy = 0;
		panelStimulus.add(textField_5, gbc_textField_5);
		textField_5.setColumns(10);

		JLabel lblNewLabel_11 = new JLabel("Consecutive Stimulus Delay");
		lblNewLabel_11.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblNewLabel_11 = new GridBagConstraints();
		gbc_lblNewLabel_11.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_11.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_11.gridx = 0;
		gbc_lblNewLabel_11.gridy = 1;
		panelStimulus.add(lblNewLabel_11, gbc_lblNewLabel_11);

		textField_4 = new JTextField();
		textField_4.setText("25");
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 1;
		panelStimulus.add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);

		JPanel panelTrainingRange = new JPanel();
		panelReferenceData.add(panelTrainingRange);
		panelTrainingRange.setBorder(new TitledBorder(null, "Training Range",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTrainingRange.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_8 = new JPanel();
		panelTrainingRange.add(panel_8);

		JLabel lblPathwayBeginning = new JLabel("Pathway beginning:");
		panel_8.add(lblPathwayBeginning);

		tfPathwayBeginning = new JTextField();
		tfPathwayBeginning.setText(""
				+ SOEASYParameters.getInstance().getParameter(
						SOEASYParameters.PATHWAY_BEGINNING));
		tfPathwayBeginning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String text = tfPathwayBeginning.getText();
					double value = Double.parseDouble(text);
					if (value <= 0.0) {
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								"The value should be higher than 0.0.",
								"Invalid Interval Value",
								JOptionPane.ERROR_MESSAGE);
					}
					tfPathwayBeginning.setText(String.valueOf(value));
					SOEASYParameters parameters = SOEASYController
							.getInstance().getParameters();
					parameters.setParameter(SOEASYParameters.PATHWAY_BEGINNING,
							tfPathwayBeginning.getText());

					generateReferenceCharts();
				} catch (Exception e) {
					JOptionPane
							.showMessageDialog(
									ApplicationWindow.this.frmSoeasyWorkbench,
									"Please enter a real number value.",
									"Invalid Interval Value",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel_8.add(tfPathwayBeginning);
		tfPathwayBeginning.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPathwayBeginning.setColumns(4);

		JLabel lblPathwayEnd = new JLabel("Pathway end:");
		panel_8.add(lblPathwayEnd);

		tfPathwayEnd = new JTextField();
		tfPathwayEnd.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPathwayEnd.setText(""
				+ SOEASYParameters.getInstance().getParameter(
						SOEASYParameters.PATHWAY_END));
		tfPathwayEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String text = tfPathwayEnd.getText();
					double value = Double.parseDouble(text);
					if (value <= 0.0) {
						JOptionPane.showMessageDialog(
								ApplicationWindow.this.frmSoeasyWorkbench,
								"The value should be higher than 0.0.",
								"Invalid Interval Value",
								JOptionPane.ERROR_MESSAGE);
					}
					tfPathwayEnd.setText(String.valueOf(value));
					SOEASYParameters parameters = SOEASYController
							.getInstance().getParameters();
					parameters.setParameter(SOEASYParameters.PATHWAY_END,
							tfPathwayEnd.getText());

					generateReferenceCharts();
				} catch (Exception e2) {
					JOptionPane
							.showMessageDialog(
									ApplicationWindow.this.frmSoeasyWorkbench,
									"Please enter a real number value.",
									"Invalid Interval Value",
									JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		panel_8.add(tfPathwayEnd);
		tfPathwayEnd.setColumns(4);

		JPanel panel_7 = new JPanel();
		panelReferenceData.add(panel_7);
		panel_7.setBorder(new TitledBorder(null, "Diagram Properties",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setLayout(new GridLayout(2, 3, 0, 0));

		JLabel lblMinTimeRange = new JLabel("Minimum range value:");
		lblMinTimeRange.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_7.add(lblMinTimeRange);

		tfDiagramRangeFrom = new JTextField();
		tfDiagramRangeFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDiagramTimeRanges();
			}
		});
		tfDiagramRangeFrom.setEditable(false);
		panel_7.add(tfDiagramRangeFrom);
		tfDiagramRangeFrom.setHorizontalAlignment(SwingConstants.TRAILING);
		tfDiagramRangeFrom.setText("-100.0");
		tfDiagramRangeFrom.setColumns(4);

		chckbxAutoAdjustTimeRange = new JCheckBox("Auto-adjust range");
		chckbxAutoAdjustTimeRange.setEnabled(false);
		chckbxAutoAdjustTimeRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean selected = chckbxAutoAdjustTimeRange.isSelected();
				tfDiagramRangeFrom.setEditable(!selected);
				tfDiagramRangeTo.setEditable(!selected);
				updateDiagramTimeRanges();
			}
		});
		chckbxAutoAdjustTimeRange.setSelected(true);
		panel_7.add(chckbxAutoAdjustTimeRange);

		JLabel lblNewLabel_9 = new JLabel("Maximum range value:");
		lblNewLabel_9.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_7.add(lblNewLabel_9);

		tfDiagramRangeTo = new JTextField();
		tfDiagramRangeTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDiagramTimeRanges();
			}
		});
		tfDiagramRangeTo.setEditable(false);
		panel_7.add(tfDiagramRangeTo);
		tfDiagramRangeTo.setText("200.0");
		tfDiagramRangeTo.setHorizontalAlignment(SwingConstants.TRAILING);
		tfDiagramRangeTo.setColumns(4);

		JLabel lblNewLabel_10 = new JLabel("");
		panel_7.add(lblNewLabel_10);

		JPanel panel_21 = new JPanel();
		panelViewerAgent.add(panel_21);
		panel_21.setLayout(new BorderLayout(0, 0));

		JPanel panel_12 = new JPanel();
		panel_21.add(panel_12);
		panel_12.setLayout(new GridLayout(2, 1, 0, 0));

		panelReferencePSFPreview = new PSFPanel(frmSoeasyWorkbench,
				PSFPanel.REFERENCE_MODE);
		panel_12.add(panelReferencePSFPreview);
		panelReferencePSFPreview.setLayout(new BorderLayout(0, 0));

		panelReferencePSFEmptyBinsHistogram = new HistogramChartPanel(
				frmSoeasyWorkbench, "Consecutive Empty Bins in PSF",
				"empty bin size", "count", false);
		panel_12.add(panelReferencePSFEmptyBinsHistogram);

		JPanel panel_14 = new JPanel();
		panelViewerAgent.add(panel_14);
		panel_14.setLayout(new BorderLayout(0, 0));

		JPanel panel_10 = new JPanel();
		panel_14.add(panel_10, BorderLayout.CENTER);
		panel_10.setLayout(new GridLayout(2, 1, 0, 0));

		panelSettingsRefPSFCUSUM = new PSFCUSUMPanel(frmSoeasyWorkbench,
				PSFCUSUMPanel.REFERENCE_MODE);
		panel_10.add(panelSettingsRefPSFCUSUM);

		panelReferencePSFCUSUMDerivativePreview = new PSFCUSUMDerivativePanel(
				frmSoeasyWorkbench, PSFCUSUMDerivativePanel.REFERENCE_MODE);
		panel_10.add(panelReferencePSFCUSUMDerivativePreview);

		JPanel panelFunctionalAgents = new JPanel();
		tabbedPaneSettings.addTab("Functional Agents", null,
				panelFunctionalAgents, null);
		panelFunctionalAgents.setLayout(new GridLayout(0, 4, 0, 0));

		JPanel panelNeuronAgents = new JPanel();
		panelFunctionalAgents.add(panelNeuronAgents);
		panelNeuronAgents.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Neuron Agents",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panelNeuronAgents.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_6.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_6.setBorder(new TitledBorder(null, "Nominal Behaviors",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNeuronAgents.add(panel_6);

		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton(
				"Integrate & fire model");
		rdbtnNewRadioButton_1.setSelected(true);
		panel_6.add(rdbtnNewRadioButton_1);

		JPanel panelAdaptive = new JPanel();
		panelNeuronAgents.add(panelAdaptive);
		panelAdaptive.setBorder(new TitledBorder(null, "Adaptive Behaviors",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panelAnnoyanceLevels = new JPanel();
		panelAnnoyanceLevels.setBorder(new TitledBorder(null,
				"Annoyance Levels", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panelAdaptive.add(panelAnnoyanceLevels);
		panelAnnoyanceLevels.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel lblNewLabel_2 = new JLabel("Re-organization: ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAnnoyanceLevels.add(lblNewLabel_2);

		final JSpinner spinner_3 = new JSpinner();
		spinner_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				String rlevel = spinner_3.getValue().toString();
				SOEASYParameters.getInstance().setParameter(
						SOEASYParameters.REORGANIZATION_ANNYOYANCE_LEVEL,
						rlevel);
				generateReferenceCharts();
			}
		});
		spinner_3.setModel(new SpinnerNumberModel(new Integer(20), null, null,
				new Integer(1)));
		panelAnnoyanceLevels.add(spinner_3);

		JLabel lblNewLabel_3 = new JLabel("Evolution: ");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.TRAILING);
		panelAnnoyanceLevels.add(lblNewLabel_3);

		final JSpinner spinner_4 = new JSpinner();
		spinner_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				String elevel = spinner_4.getValue().toString();
				SOEASYParameters.getInstance().setParameter(
						SOEASYParameters.EVOLUTION_ANNYOYANCE_LEVEL, elevel);
				generateReferenceCharts();
			}
		});
		spinner_4.setModel(new SpinnerNumberModel(new Integer(40), null, null,
				new Integer(1)));
		panelAnnoyanceLevels.add(spinner_4);

		JLabel lblNewLabel_4 = new JLabel("Max. helping distance:");
		panelAdaptive.add(lblNewLabel_4);

		textField_3 = new JTextField();
		textField_3.setHorizontalAlignment(SwingConstants.TRAILING);
		textField_3.setText("4.0");
		panelAdaptive.add(textField_3);
		textField_3.setColumns(4);

		JPanel panelSensoryNeuron = new JPanel();
		panelFunctionalAgents.add(panelSensoryNeuron);
		panelSensoryNeuron.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Sensory Neuron Agent",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));

		JPanel panelInterneuron = new JPanel();
		panelFunctionalAgents.add(panelInterneuron);
		panelInterneuron.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Interneuron Agent",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panelInterneuron.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panelBiological = new JPanel();
		panelInterneuron.add(panelBiological);
		panelBiological.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Nominal Behaviors",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JLabel lblNewLabel_1 = new JLabel("Resting AHP Level (mV):");
		panelBiological.add(lblNewLabel_1);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.TRAILING);
		textField.setText("0.50");
		panelBiological.add(textField);
		textField.setColumns(5);

		JPanel panelMotoneuron = new JPanel();
		panelFunctionalAgents.add(panelMotoneuron);
		panelMotoneuron.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Motoneuron Agent",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));

		JPanel panelMNNominal = new JPanel();
		panelMNNominal.setBorder(new TitledBorder(null, "Nominal Behaviors",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMotoneuron.add(panelMNNominal);
		panelMNNominal.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panelAHPCurve = new JPanel();
		panelAHPCurve.setBorder(new TitledBorder(null, "AHP Curve",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMNNominal.add(panelAHPCurve);
		panelAHPCurve.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNewLabel_5 = new JLabel("");
		lblNewLabel_5
				.setIcon(new ImageIcon(
						"C:\\Users\\Gurcan\\workspace\\SO-EASY-GUI\\icons\\ahp_curve_1.png"));
		panelAHPCurve.add(lblNewLabel_5);

		JPanel panelScenario = new JPanel();
		tabbedPaneSettings.addTab("Scenario", null, panelScenario, null);

		JPanel panelGlobalBehavior = new JPanel();
		tabbedPane.addTab("Macroscopic Behavior", new ImageIcon(
				"./icons/24x24/x-office-presentation.png"),
				panelGlobalBehavior, null);
		panelGlobalBehavior.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPaneMacroBehavior = new JTabbedPane(JTabbedPane.TOP);
		panelGlobalBehavior.add(tabbedPaneMacroBehavior, BorderLayout.CENTER);

		JPanel panelPSFTab = new JPanel();
		tabbedPaneMacroBehavior.addTab("Peristimulus Frequencygram (PSF)",
				null, panelPSFTab, null);
		panelPSFTab.setLayout(new BorderLayout(0, 0));
		
		panelPSFDiagrams = new JPanel();
		panelPSFTab.add(panelPSFDiagrams, BorderLayout.CENTER);
		panelPSFDiagrams.setLayout(new CardLayout(0, 0));

		JPanel panelPSFDiagramsSeparate = new JPanel();
		panelPSFDiagrams.add(panelPSFDiagramsSeparate, "PSF-Separate");
		panelPSFDiagramsSeparate.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel panelReferenceMNDischarges = new JPanel();
		panelPSFDiagramsSeparate.add(panelReferenceMNDischarges);
		panelReferenceMNDischarges.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Reference MN Discharges",
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panelReferenceMNDischarges.setLayout(new GridLayout(0, 3, 0, 0));
		panelReferencePSF = new PSFPanel(frmSoeasyWorkbench,
				PSFPanel.REFERENCE_MODE);
		panelReferenceMNDischarges.add(panelReferencePSF);

		panelReferencePSFCUSUMDerivative = new PSFCUSUMDerivativePanel(
				frmSoeasyWorkbench, PSFCUSUMDerivativePanel.REFERENCE_MODE);
		panelReferenceMNDischarges.add(panelReferencePSFCUSUMDerivative);

		panelReferencePSFCUSUM = new PSFCUSUMPanel(frmSoeasyWorkbench,
				PSFCUSUMPanel.REFERENCE_MODE);
		panelReferenceMNDischarges.add(panelReferencePSFCUSUM);

		JPanel panelSimulatedMNDischarges = new JPanel();
		panelPSFDiagramsSeparate.add(panelSimulatedMNDischarges);
		panelSimulatedMNDischarges.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Simulated MN Discharges",
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panelSimulatedMNDischarges.setLayout(new GridLayout(0, 3, 0, 0));

		panelSimulatedPSF = new PSFPanel(frmSoeasyWorkbench,
				PSFPanel.SIMULATED_MODE);
		panelSimulatedMNDischarges.add(panelSimulatedPSF);

		panelSimulatedPSFCUSUMDerivative = new PSFCUSUMDerivativePanel(
				frmSoeasyWorkbench, PSFCUSUMDerivativePanel.SIMULATED_MODE);
		panelSimulatedMNDischarges.add(panelSimulatedPSFCUSUMDerivative);

		panelSimulatedPSFCUSUM = new PSFCUSUMPanel(frmSoeasyWorkbench,
				PSFCUSUMPanel.SIMULATED_MODE);
		panelSimulatedMNDischarges.add(panelSimulatedPSFCUSUM);
		
				JPanel panelPSFDiagramsCombined = new JPanel();
				panelPSFDiagrams.add(panelPSFDiagramsCombined, "PSF-Combined");
				panelPSFDiagramsCombined.setLayout(new GridLayout(2, 0, 0, 0));
				
						JPanel panelGBCTop = new JPanel();
						panelGBCTop.setBorder(new TitledBorder(null,
								"Reference & Simulated MN Discharges", TitledBorder.LEADING,
								TitledBorder.TOP, null, null));
						panelPSFDiagramsCombined.add(panelGBCTop);
						panelGBCTop.setLayout(new GridLayout(0, 3, 0, 0));
						
								panelPSFCombined = new PSFPanel(frmSoeasyWorkbench,
										PSFPanel.COMBINED_MODE);
								panelGBCTop.add(panelPSFCombined);
								
										panelPSFCUSUMDerivativeCombined = new PSFCUSUMDerivativePanel(
												frmSoeasyWorkbench, PSFCUSUMDerivativePanel.COMBINED_MODE);
										panelGBCTop.add(panelPSFCUSUMDerivativeCombined);
										
												panelPSFCUSUMCombined = new PSFCUSUMPanel(frmSoeasyWorkbench,
														PSFCUSUMPanel.COMBINED_MODE);
												panelGBCTop.add(panelPSFCUSUMCombined);
												
														JPanel panelGBCBottom = new JPanel();
														panelPSFDiagramsCombined.add(panelGBCBottom);
														panelGBCBottom.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panelPSFEvaluation = new JPanel();
		panelPSFTab.add(panelPSFEvaluation, BorderLayout.SOUTH);
		panelPSFEvaluation.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Reference vs. Simulated",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPSFEvaluation.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panelPSFView = new JPanel();
		panelPSFView.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSFEvaluation.add(panelPSFView);
		
		ButtonGroup btnGrpPSF = new ButtonGroup();
		JRadioButton rdbtnPSFSeparateView = new JRadioButton("Separate view");
		rdbtnPSFSeparateView.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				CardLayout cl = (CardLayout)(panelPSFDiagrams.getLayout());
		        cl.show(panelPSFDiagrams, "PSF-Separate");//(String)evt.getItem());
			}
		});		
		rdbtnPSFSeparateView.setSelected(true);
		panelPSFView.add(rdbtnPSFSeparateView);
		btnGrpPSF.add(rdbtnPSFSeparateView);
		
		JRadioButton rdbtnPSFCombinedView = new JRadioButton("Combined view");
		rdbtnPSFCombinedView.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				CardLayout cl = (CardLayout)(panelPSFDiagrams.getLayout());
		        cl.show(panelPSFDiagrams, "PSF-Combined");//(String)evt.getItem());
			}
		});
		panelPSFView.add(rdbtnPSFCombinedView);
		btnGrpPSF.add(rdbtnPSFCombinedView);

		JPanel panelAverageEvaluation = new JPanel();
		panelAverageEvaluation.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSFEvaluation.add(panelAverageEvaluation);

		JPanel panelAverageCorrelation = new JPanel();
		panelAverageEvaluation.add(panelAverageCorrelation);

		JLabel lblPearsonCorrelation = new JLabel("Pearson correlation:");
		panelAverageCorrelation.add(lblPearsonCorrelation);

		tfPearsonCorrelation4Average = new JTextField();
		panelAverageCorrelation.add(tfPearsonCorrelation4Average);
		tfPearsonCorrelation4Average
				.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPearsonCorrelation4Average.setText("0.0");
		tfPearsonCorrelation4Average.setEditable(false);
		tfPearsonCorrelation4Average.setColumns(7);

		JPanel panelAverageSimilarity = new JPanel();
		panelAverageEvaluation.add(panelAverageSimilarity);

		JLabel lblSimilarity = new JLabel("Similarity:");
		panelAverageSimilarity.add(lblSimilarity);

		tfSimilarity4Average = new JTextField();
		panelAverageSimilarity.add(tfSimilarity4Average);
		tfSimilarity4Average.setHorizontalAlignment(SwingConstants.TRAILING);
		tfSimilarity4Average.setText("0.0 %");
		tfSimilarity4Average.setEditable(false);
		tfSimilarity4Average.setColumns(7);

		JPanel panelCUSUMEvaluation = new JPanel();
		panelCUSUMEvaluation.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSFEvaluation.add(panelCUSUMEvaluation);
		panelCUSUMEvaluation.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panelPSFCUSUMCorrelation = new JPanel();
		panelCUSUMEvaluation.add(panelPSFCUSUMCorrelation);

		JLabel lblPsfcusumPearsonCorrelation = new JLabel(
				"Pearson correlation:");
		panelPSFCUSUMCorrelation.add(lblPsfcusumPearsonCorrelation);

		tfPearsonCorrelation = new JTextField();
		panelPSFCUSUMCorrelation.add(tfPearsonCorrelation);
		tfPearsonCorrelation.setText("0.0");
		tfPearsonCorrelation.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPearsonCorrelation.setEditable(false);
		tfPearsonCorrelation.setColumns(7);

		JPanel panelCUSUMSimilarity = new JPanel();
		panelCUSUMEvaluation.add(panelCUSUMSimilarity);

		JLabel lblNewLabel_7 = new JLabel("Similarity: ");
		panelCUSUMSimilarity.add(lblNewLabel_7);

		tfSimilarity = new JTextField();
		panelCUSUMSimilarity.add(tfSimilarity);
		tfSimilarity.setText("0.0 %");
		tfSimilarity.setHorizontalAlignment(SwingConstants.TRAILING);
		tfSimilarity.setEditable(false);
		tfSimilarity.setColumns(7);

		JPanel panelPSTHTab = new JPanel();
		tabbedPaneMacroBehavior.addTab("Peristimulus Histogram (PSTH)", null,
				panelPSTHTab, null);
		panelPSTHTab.setLayout(new BorderLayout(0, 0));
		
		panelPSTHDiagrams = new JPanel();
		panelPSTHTab.add(panelPSTHDiagrams, BorderLayout.CENTER);
		panelPSTHDiagrams.setLayout(new CardLayout(0, 0));

		JPanel panelPSTHDiagramsSeparate = new JPanel();
		panelPSTHDiagrams.add(panelPSTHDiagramsSeparate, "PSTH-Separate");
		panelPSTHDiagramsSeparate.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel panelReferencePSTHDiagrams = new JPanel();
		panelReferencePSTHDiagrams.setBorder(new TitledBorder(null,
				"Reference MN Discharges", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panelPSTHDiagramsSeparate.add(panelReferencePSTHDiagrams);
		panelReferencePSTHDiagrams.setLayout(new GridLayout(0, 3, 0, 0));

		panelRefPSTH = new PSTHPanel(frmSoeasyWorkbench,
				PSTHPanel.REFERENCE_MODE);
		panelReferencePSTHDiagrams.add(panelRefPSTH);

		panelReferencePSTHCUSUM = new PSTHCUSUMPanel(frmSoeasyWorkbench,
				PSTHCUSUMPanel.REFERENCE_MODE);
		panelReferencePSTHDiagrams.add(panelReferencePSTHCUSUM);
		
		panelRefPSTHCUSUMDerivative = new PSTHCUSUMDerivativePanel(frmSoeasyWorkbench, PSTHCUSUMDerivativePanel.REFERENCE_MODE);
		panelReferencePSTHDiagrams.add(panelRefPSTHCUSUMDerivative);

		JPanel panelSimulatedPSTHDiagrams = new JPanel();
		panelSimulatedPSTHDiagrams.setBorder(new TitledBorder(null,
				"Simulated MN Discharges", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panelPSTHDiagramsSeparate.add(panelSimulatedPSTHDiagrams);
		panelSimulatedPSTHDiagrams.setLayout(new GridLayout(0, 3, 0, 0));

		panelSimulatedPSTH = new PSTHPanel(frmSoeasyWorkbench,
				PSTHPanel.SIMULATED_MODE);
		panelSimulatedPSTHDiagrams.add(panelSimulatedPSTH);

		panelSimulatedPSTHCUSUM = new PSTHCUSUMPanel(frmSoeasyWorkbench,
				PSTHCUSUMPanel.SIMULATED_MODE);
		panelSimulatedPSTHDiagrams.add(panelSimulatedPSTHCUSUM);
		
		panelSimulatedPSTHCUSUMDerivative = new PSTHCUSUMDerivativePanel(frmSoeasyWorkbench, PSTHCUSUMDerivativePanel.SIMULATED_MODE);
		panelSimulatedPSTHDiagrams.add(panelSimulatedPSTHCUSUMDerivative);
		
		JPanel panelPSTHDiagramsCombined = new JPanel();
		panelPSTHDiagrams.add(panelPSTHDiagramsCombined, "PSTH-Combined");
		panelPSTHDiagramsCombined.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel panelPSTHCombinedTop = new JPanel();
		panelPSTHCombinedTop.setBorder(new TitledBorder(null, "Reference & Simulated MN Discharges", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPSTHDiagramsCombined.add(panelPSTHCombinedTop);
		panelPSTHCombinedTop.setLayout(new GridLayout(0, 3, 0, 0));
		
		panelPSTHCombined = new PSTHPanel(frmSoeasyWorkbench, PSTHPanel.COMBINED_MODE);
		panelPSTHCombinedTop.add(panelPSTHCombined);
		
		panelPSTHCUSUMCombined = new PSTHCUSUMPanel(frmSoeasyWorkbench, PSTHPanel.COMBINED_MODE);
		panelPSTHCombinedTop.add(panelPSTHCUSUMCombined);
		
		panelPSTHCUSUMDerivativeCombined = new PSTHCUSUMDerivativePanel(frmSoeasyWorkbench, PSTHCUSUMDerivativePanel.COMBINED_MODE);
		panelPSTHCombinedTop.add(panelPSTHCUSUMDerivativeCombined);
		
		JPanel panelPSTHCombinedBottom = new JPanel();
		panelPSTHDiagramsCombined.add(panelPSTHCombinedBottom);

		JPanel panelPSTHEvaluation = new JPanel();
		panelPSTHEvaluation.setBorder(new TitledBorder(UIManager

		.getBorder("TitledBorder.border"), "Reference vs. Simulated",

		TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPSTHTab.add(panelPSTHEvaluation, BorderLayout.SOUTH);
		panelPSTHEvaluation.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panelPSTHView = new JPanel();
		panelPSTHView.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSTHEvaluation.add(panelPSTHView);
		
		ButtonGroup btnGrpPSTH = new ButtonGroup();
		JRadioButton rdbtnPSTHSeparateView = new JRadioButton("Separate view");
		rdbtnPSTHSeparateView.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				CardLayout cl = (CardLayout)(panelPSTHDiagrams.getLayout());
		        cl.show(panelPSTHDiagrams, "PSTH-Separate");//(String)evt.getItem());
			}
		});
		rdbtnPSTHSeparateView.setSelected(true);
		panelPSTHView.add(rdbtnPSTHSeparateView);
		btnGrpPSTH.add(rdbtnPSTHSeparateView);
		
		JRadioButton rdbtnPSTHCombinedView = new JRadioButton("Combined view");
		rdbtnPSTHCombinedView.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				CardLayout cl = (CardLayout)(panelPSTHDiagrams.getLayout());
		        cl.show(panelPSTHDiagrams, "PSTH-Combined");//(String)evt.getItem());
			}
		});
		panelPSTHView.add(rdbtnPSTHCombinedView);
		btnGrpPSTH.add(rdbtnPSTHCombinedView);

		JPanel panelPSTHCUSUMEvaluation = new JPanel();
		panelPSTHCUSUMEvaluation.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSTHEvaluation.add(panelPSTHCUSUMEvaluation);
		panelPSTHCUSUMEvaluation.setLayout(new FlowLayout(FlowLayout.CENTER, 5,
				5));

		JPanel panelPSTHCUSUMCorrelation = new JPanel();
		panelPSTHCUSUMEvaluation.add(panelPSTHCUSUMCorrelation);

		JLabel label_2 = new JLabel("Pearson correlation:");
		panelPSTHCUSUMCorrelation.add(label_2);

		tfPSTHCUSUMPearsonCorrelation = new JTextField();
		tfPSTHCUSUMPearsonCorrelation.setText("0.0");
		tfPSTHCUSUMPearsonCorrelation
				.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPSTHCUSUMPearsonCorrelation.setEditable(false);
		tfPSTHCUSUMPearsonCorrelation.setColumns(7);
		panelPSTHCUSUMCorrelation.add(tfPSTHCUSUMPearsonCorrelation);

		JPanel panelPSTHCUSUMSimilarity = new JPanel();
		panelPSTHCUSUMEvaluation.add(panelPSTHCUSUMSimilarity);

		JLabel label_3 = new JLabel("Similarity: ");
		panelPSTHCUSUMSimilarity.add(label_3);

		tfPSTHCUSUMSimilarity = new JTextField();
		tfPSTHCUSUMSimilarity.setText("0.0 %");
		tfPSTHCUSUMSimilarity.setHorizontalAlignment(SwingConstants.TRAILING);
		tfPSTHCUSUMSimilarity.setEditable(false);
		tfPSTHCUSUMSimilarity.setColumns(7);
		panelPSTHCUSUMSimilarity.add(tfPSTHCUSUMSimilarity);

		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelPSTHEvaluation.add(panel_9);

		JPanel panelStatistics = new JPanel();
		tabbedPane.addTab("Learning Statistics", new ImageIcon(
				"./icons/24x24/application-octet-stream.png"), panelStatistics,
				null);
		panelStatistics.setLayout(new BorderLayout(0, 0));

		JPanel panelStatisticsDiagrams = new JPanel();
		panelStatistics.add(panelStatisticsDiagrams, BorderLayout.CENTER);
		panelStatisticsDiagrams.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel panelStatsTop = new JPanel();
		panelStatsTop.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelStatisticsDiagrams.add(panelStatsTop);
		panelStatsTop.setLayout(new GridLayout(0, 3, 0, 0));

		panelNeuronsByTime = new NeuronEvolutionPanel(frmSoeasyWorkbench);
		panelStatsTop.add(panelNeuronsByTime);

		panelViewersLastFeedbacks = new ViewersLastFeedbacksForMN(frmSoeasyWorkbench);
		panelStatsTop.add(panelViewersLastFeedbacks);
		
				panelCriticality = new CriticalityPanel(frmSoeasyWorkbench);
				panelStatsTop.add(panelCriticality);

		JPanel panelStatsBottom = new JPanel();
		panelStatsBottom.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panelStatisticsDiagrams.add(panelStatsBottom);
		panelStatsBottom.setLayout(new GridLayout(0, 3, 0, 0));

		panelSynapsesByTime = new SynapseEvolutionPanel(frmSoeasyWorkbench);
		panelStatsBottom.add(panelSynapsesByTime);
		
				panelViewersFeedbackEvolution = new FeedbacksEvolutionPanel(frmSoeasyWorkbench);
				panelStatsBottom.add(panelViewersFeedbackEvolution);
				
				panelCriticalityDistribution = new HistogramChartPanel(frmSoeasyWorkbench, "Criticality Distribution",
						"criticality", "number of neurons", false);
				panelStatsBottom.add(panelCriticalityDistribution);
		

		JPanel panelNeurophysiological = new JPanel();
		tabbedPane.addTab("Neurophysiological Statistics", new ImageIcon(
				"./icons/24x24/applications-office.png"),
				panelNeurophysiological, null);
		panelNeurophysiological.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel panel_5 = new JPanel();
		panelNeurophysiological.add(panel_5);
		panel_5.setLayout(new GridLayout(0, 3, 0, 0));

		panelSynapsesOnMN = new HistogramChartPanel(frmSoeasyWorkbench,
				"Synapses on Motoneuron", "peristimulus time (ms)", "count",
				true);
		panel_5.add(panelSynapsesOnMN);

		JPanel panel_15 = new JPanel();
		panel_5.add(panel_15);

		JPanel panel_16 = new JPanel();
		panel_5.add(panel_16);

		JPanel panel_17 = new JPanel();
		panelNeurophysiological.add(panel_17);
		panel_17.setLayout(new GridLayout(0, 3, 0, 0));

		panelEffectOnMN = new LineChartPanel(frmSoeasyWorkbench,
				"Net Effect on Motoneuron", "peristimulus time (ms)",
				"psp amplitude (mV)", false);
		panel_17.add(panelEffectOnMN);

		JPanel panel_19 = new JPanel();
		panel_17.add(panel_19);

		JPanel panel_20 = new JPanel();
		panel_17.add(panel_20);
	}

	private void updateDiagramTimeRanges() {
		if (chckbxAutoAdjustTimeRange.isSelected()) {
			// for all diagrams, update diagram time ranges
			panelReferencePSFPreview.setAutoRangeDomainAxis(true);
			panelReferencePSF.setAutoRangeDomainAxis(true);
			panelSimulatedPSF.setAutoRangeDomainAxis(true);
			panelSimulatedPSFCUSUM.setAutoRangeDomainAxis(true);
			panelPSFCombined.setAutoRangeDomainAxis(true);
			panelPSFCUSUMCombined.setAutoRangeDomainAxis(true);
			panelReferencePSFCUSUMDerivativePreview
					.setAutoRangeDomainAxis(true);
			panelReferencePSFCUSUMDerivative.setAutoRangeDomainAxis(true);
			panelSimulatedPSFCUSUMDerivative.setAutoRangeDomainAxis(true);
			panelPSFCUSUMDerivativeCombined.setAutoRangeDomainAxis(true);
			panelRefPSTH.setAutoRangeDomainAxis(true);
			panelPSTHCombined.setAutoRangeDomainAxis(true);
			panelReferencePSTHCUSUM.setAutoRangeDomainAxis(true);
			panelSimulatedPSTH.setAutoRangeDomainAxis(true);
			panelSimulatedPSTHCUSUM.setAutoRangeDomainAxis(true);
			panelPSTHCUSUMCombined.setAutoRangeDomainAxis(true);
			panelSettingsRefPSFCUSUM.setAutoRangeDomainAxis(true);
			panelReferencePSFCUSUM.setAutoRangeDomainAxis(true);
			panelRefPSTHCUSUMDerivative.setAutoRangeDomainAxis(true);
			panelSimulatedPSTHCUSUMDerivative.setAutoRangeDomainAxis(true);
			panelPSTHCUSUMDerivativeCombined.setAutoRangeDomainAxis(true);
		} else { // manual range
			String lowerText = tfDiagramRangeFrom.getText();
			double lower = Double.parseDouble(lowerText);
			String upperText = tfDiagramRangeTo.getText();
			double upper = Double.parseDouble(upperText);

			// for all diagrams, update diagram time ranges
			panelReferencePSFPreview.configureDomainAxisRange(lower, upper);
			panelReferencePSF.configureDomainAxisRange(lower, upper);
			panelSimulatedPSF.configureDomainAxisRange(lower, upper);
			panelSimulatedPSFCUSUM.configureDomainAxisRange(lower, upper);
			panelPSFCombined.configureDomainAxisRange(lower, upper);
			panelPSFCUSUMCombined.configureDomainAxisRange(lower, upper);
			panelReferencePSFCUSUMDerivativePreview.configureDomainAxisRange(
					lower, upper);
			// ((NumberAxis)
			// plot.getRangeAxis()).setAutoRangeIncludesZero(false);
			panelReferencePSFCUSUMDerivative.configureDomainAxisRange(lower,
					upper);
			// ((NumberAxis)
			// plot.getRangeAxis()).setAutoRangeIncludesZero(false);
			panelSimulatedPSFCUSUMDerivative.configureDomainAxisRange(lower,
					upper);
			panelPSFCUSUMDerivativeCombined.configureDomainAxisRange(lower,
					upper);
			panelRefPSTH.configureDomainAxisRange(lower, upper);
			panelPSTHCombined.configureDomainAxisRange(lower, upper);
			panelReferencePSTHCUSUM.configureDomainAxisRange(lower, upper);
			panelSimulatedPSTH.configureDomainAxisRange(lower, upper);
			panelSimulatedPSTHCUSUM.configureDomainAxisRange(lower, upper);
			panelPSTHCUSUMCombined.configureDomainAxisRange(lower, upper);
			panelSettingsRefPSFCUSUM.configureDomainAxisRange(lower, upper);
			panelReferencePSFCUSUM.configureDomainAxisRange(lower, upper);
			panelRefPSTHCUSUMDerivative.configureDomainAxisRange(lower, upper);
			panelSimulatedPSTHCUSUMDerivative.configureDomainAxisRange(lower,
					upper);
			panelPSTHCUSUMDerivativeCombined.configureDomainAxisRange(lower,
					upper);
		}
	}

	private void generateReferenceCharts() {
		panelReferencePSFPreview.update();
		panelReferencePSF.update();
		panelPSFCombined.update();
		panelSettingsRefPSFCUSUM.update();
		panelReferencePSFCUSUM.update();
		panelReferencePSFCUSUMDerivativePreview.update();
		panelReferencePSFCUSUMDerivative.update();
		panelRefPSTH.update();
		panelPSTHCombined.update();
		panelReferencePSTHCUSUM.update();
		panelPSTHCUSUMCombined.update();
		panelRefPSTHCUSUMDerivative.update();

		MNDischargeRates dataSet = SOEASYController.getInstance().getDataSet();
		StaticPSF referencePSF = dataSet.getReferencePSF();
		XYSeries consEmptyBins = referencePSF.getConsecutiveEmptyBins()
				.toXYSeries("");
		XYSeriesCollection datasetC = new XYSeriesCollection();
		datasetC.addSeries(consEmptyBins);
		panelReferencePSFEmptyBinsHistogram.updateDataset(datasetC);
		XYPlot plot = (XYPlot) panelReferencePSFEmptyBinsHistogram.getChart()
				.getPlot();
		plot.getDomainAxis().setRange(0.5, 100.0);
		plot.getRangeAxis().setRange(0.0, 100.0);
		
		// criticality distribution histogram
		XYSeries criticalityDist = SOEASYEnvironment.getInstance().getCriticalityDistribution().toXYSeries("");
		XYSeriesCollection datasetK = new XYSeriesCollection();
		datasetK.addSeries(criticalityDist);
		panelCriticalityDistribution.updateDataset(datasetK);
		plot = (XYPlot) panelCriticalityDistribution.getChart()
				.getPlot();
		plot.getDomainAxis().setRange(0.5, 100.0);				
		

		int consecutiveEmptyBinCount = referencePSF
				.getConsecutiveEmptyBinCount();
		tfConsecutiveEmptyBins.setText("" + consecutiveEmptyBinCount);		

		// now update their time ranges
		updateDiagramTimeRanges();
	}

	/**
	 * Used to refresh components during simulation run.
	 * 
	 */
	class RefreshingTask extends TimerTask {

		private CuSum simulatedPSFCUSUM;

		@Override
		public void run() {
			if (SOEASYController.getInstance().isSimulationRunning()) {
				double currentTick = SOEASYController.getInstance()
						.getCurrentTick();
				tfCurrentTick.setText(new DecimalFormat("###,###,###,###.#")
						.format(currentTick) + " ms");

				double currentTime = SOEASYController.getInstance()
						.getCurrentTime();
				tfCurrentTime.setText(new DecimalFormat("###,###,###,###.#")
						.format(currentTime) + " ms");

				// refresh average correlation
				MNDischargeRates dataSet = SOEASYController.getInstance()
						.getDataSet();
				simulatedPSFCUSUM = dataSet.getSimulatedPSFCUSUM();

				// refresh diagrams
				panelSimulatedPSF.update();
				panelSimulatedPSFCUSUMDerivative.update();
				panelSimulatedPSFCUSUM.update();

				panelPSFCombined.update();
				panelPSFCUSUMDerivativeCombined.update();
				panelPSFCUSUMCombined.update();

				panelSimulatedPSTH.update();
				panelSimulatedPSTHCUSUM.update();
				panelPSTHCombined.update();
				panelPSTHCUSUMCombined.update();
				panelSimulatedPSTHCUSUMDerivative.update();
				panelPSTHCUSUMDerivativeCombined.update();

				// get training area
				double beginning = SOEASYParameters.getInstance().getParameter(
						SOEASYParameters.PATHWAY_BEGINNING);
				double end = SOEASYParameters.getInstance().getParameter(
						SOEASYParameters.PATHWAY_END);

				CuSum referencePSFCUSUM = SOEASYController.getInstance()
						.getDataSet().getReferencePSFCUSUM();
				double pearsonCorrelation = Correlation.getPearsonCorrelation(
						referencePSFCUSUM.getDerivativeXYSeries(),
						simulatedPSFCUSUM.getDerivativeXYSeries(), beginning,
						end);
				tfPearsonCorrelation4Average.setText(new DecimalFormat(
						"#.#########").format(pearsonCorrelation));
				double similarity = pearsonCorrelation * pearsonCorrelation;
				tfSimilarity4Average.setText(new DecimalFormat("##.######"
						+ " %").format(similarity));

				// refresh psf-cusum correlation
				pearsonCorrelation = SOEASYController.getInstance()
						.getPSFCUSUMCorrelation();
				tfPearsonCorrelation.setText(new DecimalFormat("#.#########")
						.format(pearsonCorrelation));
				similarity = pearsonCorrelation * pearsonCorrelation;
				// here put the similarity information more detailed
				tfSimilarity.setText(new DecimalFormat("##.######" + " %")
						.format(similarity));
				// and here put it less detailed
				tfPSFCUSUMSimilarity.setText(new DecimalFormat("##.##" + " %")
						.format(similarity));

				// refresh psth-cusum correlation
				pearsonCorrelation = SOEASYController.getInstance()
						.getPSTHCUSUMCorrelation();
				tfPSTHCUSUMPearsonCorrelation.setText(new DecimalFormat(
						"#.#########").format(pearsonCorrelation));
				similarity = pearsonCorrelation * pearsonCorrelation;
				// here put the similarity information more detailed
				tfPSTHCUSUMSimilarity.setText(new DecimalFormat("##.##" + " %")
						.format(similarity));

				tfViewerGoodRate.setText(SOEASYController.getInstance()
						.getViewersGoodFeedbackRate());

				panelNeuronsByTime.update();
				panelSynapsesByTime.update();
				panelViewersLastFeedbacks.update();
				panelCriticality.update();

				refreshPanelSynapsesOnMN();

				refreshPanelEffectOnMN();

				// refreshPanelGraph();

				int neuronCount = SOEASYController.getInstance()
						.getNumberOfNeurons();
				tfNumberOfNeurons.setText("" + neuronCount);

				int synapseCount = SOEASYController.getInstance()
						.getNumberOfSynapses();
				tfNumberOfSynapses.setText("" + synapseCount);
				
				
				// criticality distribution histogram
				XYSeries criticalityDist = SOEASYEnvironment.getInstance().getCriticalityDistribution().toXYSeries("");
				XYSeriesCollection datasetK = new XYSeriesCollection();
				datasetK.addSeries(criticalityDist);
				panelCriticalityDistribution.updateDataset(datasetK);
				XYPlot plot = (XYPlot) panelCriticalityDistribution.getChart()
						.getPlot();
				plot.getDomainAxis().setRange(-0.5, 100.5);
				plot.getRangeAxis().setRange(0.0, 200.0);
			}
		}

		private void refreshPanelSynapsesOnMN() {
			HistogramDataset dataset = SOEASYController.getInstance()
					.getSynapsesOnMotoneuron();

			panelSynapsesOnMN.updateDataset(dataset);
		}

		private void refreshPanelEffectOnMN() {
			XYDataset dataset = SOEASYController.getInstance()
					.getNetEffectOnMotoneuron();

			panelEffectOnMN.updateDataset(dataset);
		}
	}
}
